package ir.shahaabco;

import org.opencv.core.Mat;
import org.opencv.core.Rect;

public class ANPRNDK {
    public static class RECT {
        public long left;
        public long top;
        public long right;
        public long bottom;
    };

    public static class SPlate{
        public String str = ""; //Plate Number in English Characters
        public String str_fa = ""; //Plate Number in Farsi Characters
        public Rect rc;
        public float[] cnf = new float[2];
    };

    public static SPlate anpr_recognize_mat(Mat img)
    {
        SPlate plt = new SPlate();
        long ptr = img.getNativeObjAddr();
        //process(ptr);

        int[] rc = new int[4];//left,top,right, bottom;
        char[] char_fa = new char[30];
        plt.str = anpr_recognize((byte) 0, ptr, char_fa, plt.cnf, rc);
        if(plt.str.length() > 0)
        {
            plt.rc  = new Rect(rc[0], rc[1], rc[2]-rc[0], rc[3]-rc[1]);
            int n = 1;
            while((char_fa[n++] != 0) && (n < 15));
            plt.str_fa = new String(char_fa, 0, n-1);
        }
        return plt;
    }


    public static native String anpr_about();

    public static native short anpr_create(byte instance, String data_path, String security_code, byte log_level /*= log_short*/);

    //num_chars1 = 8, num_chars2 = 0 or 5
    public static native void anpr_set_params(byte instance, byte num_chars1, byte num_chars2, byte detect_motor, short resize_thresh);
    public static native String anpr_recognize(byte instance, long mat_ptr, char[] result_fa, float[] cnf_ptr, int[] rc_ptr);

    public static native String anpr_get_en_result(String result_fa);
}

