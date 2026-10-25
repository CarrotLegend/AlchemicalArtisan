package com.carrot123.alchemicalartisan.client;

import com.carrot123.alchemicalartisan.AlchemicalArtisan;
import com.carrot123.alchemicalartisan.entity.ExplosiveProjectile;
import com.carrot123.alchemicalartisan.item.ExplosiveItem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber(
        modid = AlchemicalArtisan.MOD_ID,
        bus = Mod.EventBusSubscriber.Bus.FORGE,
        value = Dist.CLIENT
)
public class ExplosiveTrajectoryPreview {

    private static final int MAX_STEPS = 200;
    private static final int CIRCLE_SEGMENTS = 64;
    private static final double GRAVITY = ExplosiveProjectile.ARROW_GRAVITY;
    private static final double LINE_WIDTH = 4.0D;

    private record Trajectory(List<Vec3> points, boolean hit) {
    }

    @SubscribeEvent
    public static void onRenderLevel(RenderLevelStageEvent event) {
        if (event.getStage() !=
                RenderLevelStageEvent.Stage.AFTER_PARTICLES) {
            return;
        }

        Minecraft minecraft = Minecraft.getInstance();
        Player player = minecraft.player;

        if (player == null || minecraft.level == null) {
            return;
        }

        if (!player.isUsingItem()) {
            return;
        }

        if (!(player.getUseItem().getItem()
                instanceof ExplosiveItem explosive)) {
            return;
        }

        float chargePower = ExplosiveItem.getChargePower(
                player.getTicksUsingItem()
        );

        if (chargePower < 0.1F) {
            return;
        }

        float throwSpeed = ExplosiveItem.getThrowSpeed(
                player.getTicksUsingItem()
        );

        Trajectory trajectory = calculateTrajectory(
                player,
                event.getPartialTick(),
                throwSpeed
        );

        if (trajectory.points().size() < 2) {
            return;
        }

        boolean destructive = explosive.breaksBlocks();

        int red = 255;
        int green = destructive ? 75 : 210;
        int blue = destructive ? 65 : 50;

        PoseStack pose = event.getPoseStack();
        Vec3 camera = event.getCamera().getPosition();

        pose.pushPose();
        pose.translate(-camera.x, -camera.y, -camera.z);

        MultiBufferSource.BufferSource buffers =
                minecraft.renderBuffers().bufferSource();

        RenderType lineType =
                RenderType.debugLineStrip(LINE_WIDTH);

        List<Vec3> points = trajectory.points();

        VertexConsumer consumer = buffers.getBuffer(lineType);

        drawPolyline(
                pose,
                consumer,
                points,
                red,
                green,
                blue
        );

        buffers.endBatch(lineType);

        if (trajectory.hit()) {
            Vec3 end = points.get(points.size() - 1);
            Vec3 before = points.get(points.size() - 2);

            drawArrow(
                    pose,
                    buffers,
                    lineType,
                    before,
                    end,
                    red,
                    green,
                    blue
            );

            consumer = buffers.getBuffer(lineType);

            drawCircle(
                    pose,
                    consumer,
                    end,
                    ExplosiveItem.EXPLOSION_RADIUS,
                    red,
                    green,
                    blue
            );

            buffers.endBatch(lineType);
        }

        pose.popPose();
    }

    private static Trajectory calculateTrajectory(
            Player player,
            float partialTick,
            float throwSpeed
    ) {
        Level level = player.level();

        List<Vec3> points = new ArrayList<>();

        Vec3 position = player.getEyePosition(partialTick)
                .add(0.0D, -0.1D, 0.0D);

        Vec3 velocity = Vec3.directionFromRotation(
                player.getXRot(),
                player.getYRot()
        ).scale(throwSpeed);

        Vec3 playerMotion = player.getDeltaMovement();

        velocity = velocity.add(
                playerMotion.x,
                player.onGround() ? 0.0D : playerMotion.y,
                playerMotion.z
        );

        points.add(position);

        for (int i = 0; i < MAX_STEPS; i++) {
            Vec3 next = position.add(velocity);

            BlockHitResult blockHit = level.clip(
                    new ClipContext(
                            position,
                            next,
                            ClipContext.Block.COLLIDER,
                            ClipContext.Fluid.NONE,
                            player
                    )
            );

            Vec3 blockEnd =
                    blockHit.getType() == HitResult.Type.MISS
                            ? next
                            : blockHit.getLocation();

            EntityHitResult entityHit =
                    ProjectileUtil.getEntityHitResult(
                            level,
                            player,
                            position,
                            blockEnd,
                            new AABB(position, next).inflate(1.0D),
                            entity -> entity != player
                                    && entity.isPickable()
                                    && !entity.isSpectator()
                    );

            if (entityHit != null) {
                points.add(entityHit.getLocation());
                return new Trajectory(points, true);
            }

            if (blockHit.getType() != HitResult.Type.MISS) {
                points.add(blockHit.getLocation());
                return new Trajectory(points, true);
            }

            points.add(next);

            double drag = level.getFluidState(
                    BlockPos.containing(next)
            ).is(FluidTags.WATER) ? 0.8D : 0.99D;

            velocity = velocity.scale(drag)
                    .add(0.0D, -GRAVITY, 0.0D);

            position = next;

            if (position.y < level.getMinBuildHeight() - 16) {
                break;
            }
        }

        return new Trajectory(points, false);
    }

    private static void drawPolyline(
            PoseStack pose,
            VertexConsumer consumer,
            List<Vec3> points,
            int red,
            int green,
            int blue
    ) {
        if (points.size() < 2) {
            return;
        }

        for (int i = 0; i < points.size(); i++) {
            Vec3 point = points.get(i);

            Vec3 direction;

            if (i < points.size() - 1) {
                direction = points.get(i + 1).subtract(point);
            } else {
                direction = point.subtract(points.get(i - 1));
            }

            if (direction.lengthSqr() < 1.0E-8D) {
                direction = new Vec3(0.0D, 1.0D, 0.0D);
            } else {
                direction = direction.normalize();
            }

            consumer.vertex(
                    pose.last().pose(),
                    (float) point.x,
                    (float) point.y,
                    (float) point.z
            ).color(red, green, blue, 255)
                    .normal(
                            pose.last().normal(),
                            (float) direction.x,
                            (float) direction.y,
                            (float) direction.z
                    ).endVertex();
        }
    }

    private static void drawArrow(
            PoseStack pose,
            MultiBufferSource.BufferSource buffers,
            RenderType lineType,
            Vec3 before,
            Vec3 end,
            int red,
            int green,
            int blue
    ) {
        Vec3 delta = end.subtract(before);

        if (delta.lengthSqr() < 1.0E-8D) {
            return;
        }

        Vec3 direction = delta.normalize();

        Vec3 side = direction.cross(
                new Vec3(0.0D, 1.0D, 0.0D)
        );

        if (side.lengthSqr() < 1.0E-6D) {
            side = direction.cross(
                    new Vec3(1.0D, 0.0D, 0.0D)
            );
        }

        side = side.normalize().scale(0.22D);

        Vec3 base = end.subtract(direction.scale(0.5D));

        VertexConsumer consumer = buffers.getBuffer(lineType);

        drawPolyline(
                pose,
                consumer,
                List.of(end, base.add(side)),
                red,
                green,
                blue
        );

        buffers.endBatch(lineType);

        consumer = buffers.getBuffer(lineType);

        drawPolyline(
                pose,
                consumer,
                List.of(end, base.subtract(side)),
                red,
                green,
                blue
        );

        buffers.endBatch(lineType);
    }

    private static void drawCircle(
            PoseStack pose,
            VertexConsumer consumer,
            Vec3 center,
            double radius,
            int red,
            int green,
            int blue
    ) {
        List<Vec3> circlePoints = new ArrayList<>();

        for (int i = 0; i <= CIRCLE_SEGMENTS; i++) {
            double angle = Math.PI * 2.0D * i
                    / CIRCLE_SEGMENTS;

            Vec3 point = new Vec3(
                    center.x + Math.cos(angle) * radius,
                    center.y + 0.05D,
                    center.z + Math.sin(angle) * radius
            );

            circlePoints.add(point);
        }

        drawPolyline(
                pose,
                consumer,
                circlePoints,
                red,
                green,
                blue
        );
    }
}
