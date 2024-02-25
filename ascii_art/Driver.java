package ascii_art;

import image.Image;

import java.util.Arrays;
import java.util.logging.Logger;


public class Driver {
    public static void main(String[] args) throws Exception {
        System.out.println(Arrays.toString(args));
        System.out.println(args.length);
        if (args.length != 2) {
            System.err.println("USAGE: java asciiArt ");
            return;
        }
        Image img = Image.fromFile(args[1]);
        if (img == null) {
            Logger.getGlobal().severe("Failed to open image file " + args[0]);
            return;
        }
        new Shell(img).run();
//        Image img = Image.fromFile("/Users/giladomesi/Src/OOP/Ex 4/out/production/Ex 4/board.jpeg");
//        BrightnessImgCharMatcher charMatcher = new BrightnessImgCharMatcher(img, "Ariel");
//        var chars = charMatcher.chooseChars(2, new char[]{'m', 'o'});
//        System.out.println(Arrays.deepToString(chars));
    }
}
