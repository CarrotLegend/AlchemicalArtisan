
package com.carrot123.alchemicalartisan.client;

import com.carrot123.alchemicalartisan.AlchemicalArtisan;
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

    private static final int MAX_STEPS = 120;
    private static final int CIRCLE_SEGMENTS = 64;
    private static final double GRAVITY = 0.03D;

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

        Trajectory trajectory = calculateTrajectory(
                player,
                event.getPartialTick()
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

        RenderType lineType = RenderType.debugLineStrip(2.0D);

        VertexConsumer consumer =
                buffers.getBuffer(lineType);

        List<Vec3> points = trajectory.points();

        for (int i = 1; i < points.size(); i++) {
            drawLine(
                    pose,
                    consumer,
                    points.get(i - 1),
                    points.get(i),
                    red,
                    green,
                    blue
            );
        }

        if (trajectory.hit()) {
            Vec3 end = points.get(points.size() - 1);
            Vec3 before = points.get(points.size() - 2);

            drawArrow(
                    pose,
                    consumer,
                    before,
                    end,
                    red,
                    green,
                    blue
            );

            drawCircle(
                    pose,
                    consumer,
                    end,
                    ExplosiveItem.EXPLOSION_RADIUS,
                    red,
                    green,
                    blue
            );
        }

        buffers.endBatch(RenderType.lines());
        pose.popPose();
    }

    private static Trajectory calculateTrajectory(
            Player player,
            float partialTick
    ) {
        Level level = player.level();

        List<Vec3> points = new ArrayList<>();

        Vec3 position = player.getEyePosition(partialTick)
                .add(0.0D, -0.1D, 0.0D);

        Vec3 velocity = Vec3.directionFromRotation(
                player.getXRot(),
                player.getYRot()
        ).scale(ExplosiveItem.THROW_SPEED);

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

            Vec3 blockEnd = blockHit.getType() == HitResult.Type.MISS
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

    private static void drawLine(
            PoseStack pose,
            VertexConsumer consumer,
            Vec3 from,
            Vec3 to,
            int red,
            int green,
            int blue
    ) {
        Vec3 delta = to.subtract(from);

        if (delta.lengthSqr() < 1.0E-8D) {
            return;
        }

        Vec3 direction = delta.normalize();

        consumer.vertex(
                pose.last().pose(),
                (float) from.x,
                (float) from.y,
                (float) from.z
        ).color(red, green, blue, 255)
                .normal(
                        pose.last().normal(),
                        (float) direction.x,
                        (float) direction.y,
                        (float) direction.z
                ).endVertex();

        consumer.vertex(
                pose.last().pose(),
                (float) to.x,
                (float) to.y,
                (float) to.z
        ).color(red, green, blue, 255)
                .normal(
                        pose.last().normal(),
                        (float) direction.x,
                        (float) direction.y,
                        (float) direction.z
                ).endVertex();
    }

    private static void drawArrow(
            PoseStack pose,
            VertexConsumer consumer,
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

        drawLine(
                pose,
                consumer,
                end,
                base.add(side),
                red,
                green,
                blue
        );

        drawLine(
                pose,
                consumer,
                end,
                base.subtract(side),
                red,
                green,
                blue
        );
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
        for (int i = 0; i < CIRCLE_SEGMENTS; i++) {
            double angleA = Math.PI * 2.0D * i
                    / CIRCLE_SEGMENTS;

            double angleB = Math.PI * 2.0D * (i + 1)
                    / CIRCLE_SEGMENTS;

            Vec3 a = new Vec3(
                    center.x + Math.cos(angleA) * radius,
                    center.y + 0.05D,
                    center.z + Math.sin(angleA) * radius
            );

            Vec3 b = new Vec3(
                    center.x + Math.cos(angleB) * radius,
                    center.y + 0.05D,
                    center.z + Math.sin(angleB) * radius
            );

            drawLine(
                    pose,
                    consumer,
                    a,
                    b,
                    red,
                    green,
                    blue
            );
        }
    }
}
