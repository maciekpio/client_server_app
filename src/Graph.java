public class Graph extends org.jfree.chart.ui.ApplicationFrame {
    public static final double Xmin = -5.,Xmax=5.;
    public static final int Npoint=500;

    /**
     * Constructs a new application frame.
     *
     * @param title the frame title.
     */
    public Graph(String title) {
        super(title);
    }

    public static void main(String[] args) {
        com.github.plot.Plot obj= new com.github.plot.Plot();
    }
}
