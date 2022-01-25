package meteor.plugins.banksetups;

import com.google.inject.Inject;
import meteor.ui.overlay.OverlayLayer;
import meteor.ui.overlay.OverlayPanel;
import meteor.ui.overlay.OverlayPosition;
import meteor.ui.overlay.components.ImageComponent;
import meteor.util.ImageUtil;
import net.runelite.api.Client;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;

import java.awt.*;
import java.awt.image.BufferedImage;

public class BankSetupsOverlay extends OverlayPanel {
    BufferedImage img = null;
    @Inject
    private BankSetupsOverlay(Client client)
    {
        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.ABOVE_WIDGETS);
        panelComponent.setBackgroundColor(null);
        this.client = client;
    }
    @Override
    public Dimension render(Graphics2D graphics)
    {
        panelComponent.getChildren().clear();
        if(img==null) {
            img = ImageUtil.loadImageResource(getClass(), "setups.png");
        }
        Widget incinerator = client.getWidget(WidgetInfo.BANK_INCINERATOR);
        if(img!=null&&incinerator!=null){
            if(!incinerator.isHidden()) {
                img = scale(img,incinerator.getWidth(),incinerator.getHeight());
                panelComponent.getChildren().add(new ImageComponent(img));
                net.runelite.api.Point p = incinerator.getCanvasLocation();
                int x = p.getX();
                int y = p.getY();
                panelComponent.setPreferredLocation(new java.awt.Point(x,y-(incinerator.getHeight()+10)));
                setBounds(panelComponent.getBounds());
                return panelComponent.render(graphics);
            }
        }
        return null;
    }
    static BufferedImage scale(BufferedImage src, int w, int h)
    {
        BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        int x, y;
        int ww = src.getWidth();
        int hh = src.getHeight();
        for (x = 0; x < w; x++) {
            for (y = 0; y < h; y++) {
                int col = src.getRGB(x * ww / w, y * hh / h);
                img.setRGB(x, y, col);
            }
        }
        return img;
    }
    private BufferedImage resize(BufferedImage src, int targetSize) {
        if (targetSize <= 0) {
            return src; //this can't be resized
        }
        int targetWidth = targetSize;
        int targetHeight = targetSize;
        float ratio = ((float) src.getHeight() / (float) src.getWidth());
        if (ratio <= 1) { //square or landscape-oriented image
            targetHeight = (int) Math.ceil((float) targetWidth * ratio);
        } else { //portrait image
            targetWidth = Math.round((float) targetHeight / ratio);
        }
        BufferedImage bi = new BufferedImage(targetWidth, targetHeight, src.getTransparency() == Transparency.OPAQUE ? BufferedImage.TYPE_INT_RGB : BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = bi.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR); //produces a balanced resizing (fast and decent quality)
        g2d.drawImage(src, 0, 0, targetWidth, targetHeight, null);
        g2d.dispose();
        return bi;
    }

}
