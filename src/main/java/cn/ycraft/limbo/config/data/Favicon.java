package cn.ycraft.limbo.config.data;

import com.loohp.limbo.Limbo;
import org.jetbrains.annotations.NotNull;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.Files;

public class Favicon {


    protected final @NotNull String path;
    protected final byte[] data;

    public Favicon(@NotNull String path, byte[] data) {
        this.path = path;
        this.data = data;
    }

    public @NotNull String path() {
        return path;
    }

    public byte[] data() {
        return data;
    }

    public static Favicon load(@NotNull String path) {
        File png = new File(path);
        byte[] data = null;
        if (png.exists()) {
            try {
                BufferedImage image = ImageIO.read(png);
                if (image.getHeight() == 64 && image.getWidth() == 64) {
                    data = Files.readAllBytes(png.toPath());
                } else {
                    Limbo.getInstance().getConsole().sendMessage("Unable to load server-icon.png! The image is not 64 x 64 in size!");
                }
            } catch (Exception e) {
                Limbo.getInstance().getConsole().sendMessage("Unable to load server-icon.png! Is it a png image?");
            }
        } else {
            Limbo.getInstance().getConsole().sendMessage("No server-icon.png found");
        }
        return new Favicon(path, data);
    }


}
