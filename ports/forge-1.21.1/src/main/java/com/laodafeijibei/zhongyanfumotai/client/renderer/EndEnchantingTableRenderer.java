package com.laodafeijibei.zhongyanfumotai.client.renderer;

import com.laodafeijibei.zhongyanfumotai.ZhongyanEnchantingTableMod;
import com.laodafeijibei.zhongyanfumotai.block.entity.EndEnchantingTableBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.model.BookModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public final class EndEnchantingTableRenderer implements BlockEntityRenderer<EndEnchantingTableBlockEntity> {
    private static final ResourceLocation BOOK_TEXTURE = ResourceLocation.fromNamespaceAndPath(
            ZhongyanEnchantingTableMod.MOD_ID, "textures/entity/end_enchanting_table_book.png");

    private final BookModel bookModel;

    public EndEnchantingTableRenderer(BlockEntityRendererProvider.Context context) {
        this.bookModel = new BookModel(context.bakeLayer(ModelLayers.BOOK));
    }

    @Override
    public void render(EndEnchantingTableBlockEntity table, float partialTick, PoseStack poseStack,
                       MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        poseStack.pushPose();
        poseStack.translate(0.5F, 0.75F, 0.5F);
        float animationTime = table.time + partialTick;
        poseStack.translate(0.0F, 0.1F + Mth.sin(animationTime * 0.1F) * 0.01F, 0.0F);

        float rotationDelta = table.rot - table.oRot;
        while (rotationDelta >= Math.PI) {
            rotationDelta -= Mth.TWO_PI;
        }
        while (rotationDelta < -Math.PI) {
            rotationDelta += Mth.TWO_PI;
        }
        float rotation = table.oRot + rotationDelta * partialTick;
        poseStack.mulPose(Axis.YP.rotation(-rotation));
        poseStack.mulPose(Axis.ZP.rotationDegrees(80.0F));

        float page = Mth.lerp(partialTick, table.oFlip, table.flip);
        float leftPage = Mth.clamp(Mth.frac(page + 0.25F) * 1.6F - 0.3F, 0.0F, 1.0F);
        float rightPage = Mth.clamp(Mth.frac(page + 0.75F) * 1.6F - 0.3F, 0.0F, 1.0F);
        float open = Mth.lerp(partialTick, table.oOpen, table.open);
        bookModel.setupAnim(animationTime, leftPage, rightPage, open);

        VertexConsumer vertexConsumer = bufferSource.getBuffer(RenderType.entitySolid(BOOK_TEXTURE));
        bookModel.render(poseStack, vertexConsumer, packedLight, packedOverlay, 0xFFFFFFFF);
        poseStack.popPose();
    }
}
