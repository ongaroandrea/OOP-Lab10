package it.unibo.oop.lab.reactivegui02;

import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.Toolkit;
import java.lang.reflect.InvocationTargetException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class ConcurrentGUI {

    private final JFrame frame = new JFrame();
    private final JButton btnAdd = new JButton("Increment");
    private final JButton btnSub = new JButton("Decrement");
    private final JButton btnStop = new JButton("Stop");
    private final JLabel lbl = new JLabel("0");

    public ConcurrentGUI() {
        final JPanel canvas = new JPanel();
        canvas.setLayout(new GridBagLayout());
        canvas.add(lbl);
        canvas.add(btnAdd);
        canvas.add(btnSub);
        canvas.add(btnStop);

        final Agent agent = new Agent();
        new Thread(agent).start();
        btnAdd.addActionListener(l -> {
            agent.addCounting();
        });
        btnSub.addActionListener(l -> {
            agent.subCounting();
        });
        btnStop.addActionListener(l -> {
            agent.stopCouting();
        });
        frame.setContentPane(canvas);
        final Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        final int sw = (int) screen.getWidth();
        final int sh = (int) screen.getHeight();
        frame.setTitle("SQL Test");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(sw / 2, sh / 2);
        frame.setLocationByPlatform(true);
        frame.setVisible(true);
        frame.pack();
    }
    private class Agent implements Runnable {

        private volatile boolean stop;
        private volatile int counter;
        private volatile boolean add;
        @Override
        public void run() {
            while (!this.stop) {
                try {
                    /*
                     * All the operations on the GUI must be performed by the
                     * Event-Dispatch Thread (EDT)!
                     */
                    SwingUtilities.invokeAndWait(() -> ConcurrentGUI.this.lbl.setText(Integer.toString(this.counter)));
                    counter += this.add ? +1 : -1;
                    Thread.sleep(100);
                } catch (InvocationTargetException | InterruptedException ex) {
                    /*
                     * This is just a stack trace print, in a real program there
                     * should be some logging and decent error reporting
                     */
                    ex.printStackTrace();
                }
            }
        }
        public void stopCouting() {
            this.stop = true;
            ConcurrentGUI.this.btnAdd.setEnabled(false);
            ConcurrentGUI.this.btnSub.setEnabled(false);
            ConcurrentGUI.this.btnStop.setEnabled(false);
        }
        public void addCounting() {
            this.add = true;
        }
        public void subCounting() {
            this.add = false;
        }
    }
}
