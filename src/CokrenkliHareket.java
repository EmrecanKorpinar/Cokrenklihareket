package cokrenklihareket;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;
import javax.swing.*;

public class CokrenkliHareket {
    private static final double PROJECTILE_VELOCITY = 100; // Piksel/saniye
    private final LinkedList<Projectile> projectiles = new LinkedList<>();

    public static void main(final String[] args) {
        SwingUtilities.invokeLater(() -> {
            new CokrenkliHareket();
        });
    }

    public CokrenkliHareket() {
        final JFrame frame = new JFrame("Projectile Test");
        final JPanel content = new JPanel() {
            private final Dimension size = new Dimension(500, 500);

            @Override
            public void paintComponent(final Graphics g) {
                super.paintComponent(g);
                g.setColor(Color.BLACK);
                g.drawOval(getWidth() / 2 - 2, getHeight() / 2 - 2, 15, 15);
                synchronized (projectiles) {
                    for (Projectile projectile : projectiles) {
                        projectile.render(g);
                    }
                }
            }

            @Override
            public Dimension getPreferredSize() {
                return size;
            }
        };

        content.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(final MouseEvent e) {
                makeProjectile(e, content.getWidth(), content.getHeight());
            }
        });

        content.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(final MouseEvent e) {
                makeProjectile(e, content.getWidth(), content.getHeight());
            }
        });

        final Timer repaint = new Timer(10, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                synchronized (projectiles) {
                    final Iterator<Projectile> iter = projectiles.iterator();
                    while (iter.hasNext()) {
                        final Projectile next = iter.next();
                        if (!next.valid()) {
                            iter.remove();
                        }
                        next.step(content.getWidth(), content.getHeight());
                    }
                }
                content.repaint();
            }
        });

        frame.add(content);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        repaint.start();
    }

    public void makeProjectile(final MouseEvent e, final int width, final int height) {
        final int x = width / 2;
        final int y = height / 2;
        final double angle = Math.atan2(e.getY() - y, e.getX() - x);
        final double deltaX = Math.cos(angle);
        final double deltaY = Math.sin(angle);
        synchronized (projectiles) {
            projectiles.add(new Projectile(x, y, deltaX, deltaY));
        }
    }

    public class Projectile {
        private final double velocityX;
        private final double velocityY;
        private double x;
        private double y;
        private long lastUpdate;
        private boolean valid = true;
        private final Random rnd = new Random();
        private final Color renk = new Color(rnd.nextInt(255), rnd.nextInt(255), rnd.nextInt(255));

        public Projectile(final double x, final double y, final double vx, final double vy) {
            this.x = x;
            this.y = y;
            this.velocityX = vx;
            this.velocityY = vy;
            this.lastUpdate = System.currentTimeMillis();
        }

        public boolean valid() {
            return valid;
        }

        public void destroy() {
            this.valid = false;
        }

        public void step(final int width, final int height) {
            final long time = System.currentTimeMillis();
            final long change = time - lastUpdate;

            this.x += (change / 1000D) * (velocityX * PROJECTILE_VELOCITY);
            this.y += (change / 1000D) * (velocityY * PROJECTILE_VELOCITY);
            this.lastUpdate = time;

            if (x < 0 || y < 0 || x > width || y > height) {
                destroy();
            }
        }

        public void render(Graphics g) {
            g.setColor(renk);
            g.drawOval((int) x - 2, (int) y - 2, 10, 10);
            g.fillOval((int) x - 2, (int) y - 2, 10, 10);
        }
    }
}