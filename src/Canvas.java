import imagedata.*;
import io.vavr.Function3;
import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.collection.Stream;
import objectdata.Cube;
import objectdata.MyPoligom;
import objectdata.Pyramid;
import objectdata.Xyz;
import objectops.RenderSolid;
import objectops.RenderWireframe;
import objectops.Renderer;
import org.jetbrains.annotations.NotNull;
import rasterops.*;
import transforms.*;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.*;
import java.awt.image.BufferedImage;


import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

/**
 * trida pro kresleni na platno: zobrazeni pixelu
 *
 * @author PGRF FIM UHK
 * @version 2017
 */

/* TODO:
	* X obrazek (pixel) s hloubkou
		(2 samost. obr
		nebo nova trida DepthPixel
		nebo Tuple2<Col, Double>)
	* X obrazek se z-testem
	* X rasterizer trojuhelniku
	* RendererSolid (s podporou topologie TRIANGLE_LIST)
	* teleso z trojuhelniku
	* scena
 */

public class Canvas {

	private final JFrame frame;
	private final JPanel panel;
	private final BufferedImage img;
	private @NotNull Image<Tuple2<Col, Double>> image;
	private @NotNull Image<Tuple2<Col, Double>> pomImage;
	private @NotNull Image<Tuple2<Col, Double>> emptyImage;
	private final @NotNull Presenter<Tuple2<Col, Double>, Graphics> presenter;
	private final @NotNull LineRendererLerp<Tuple2<Col, Double>> lineRenderer;
	private final @NotNull TriangleRenderer<Tuple2<Col, Double>> triangleRenderer;
	private @NotNull Camera camera;
	private final Col black = new Col(0,0,0);
	private final Col white = new Col(255,255,255);
	private final Col red = new Col(255,0,0);
	private final Col green = new Col(0,255,0);
	private final Col blue = new Col(0,0,255);
	private final Renderer renderer;
	final Cube cube = new Cube();
	final Pyramid pyramid = new Pyramid();
	private @NotNull Mat4PerspRH mat4;
	private Point2D point2D;
	private Point2D pomPoint2D = new Point2D(0,0,0);
	double mousex =0;
	double mousey = 0;
	int mody = 0;
	Vec3D vec3D = new Vec3D(-1,-1,0);
	double posunKosticky = 0;


	private int sC, sR;

	public Canvas(final int width, final int height) {
		System.out.println("Vytvořil: Michal Hvezda   Datum odevzdani: 23.05.2018");
		System.out.println("Pohyb kamery do prava/leva -> A/D; dopredu/dozadu -> W/S; nahoru/dolu -> Q/E; a rozhlizeni mysi; meneni mezi vyplni a dratovym modelem -> M; posun teles -> SPACE ");

		frame = new JFrame();

		frame.setLayout(new BorderLayout());
		frame.setTitle("UHK FIM PGRF : " + this.getClass().getName());
		frame.setResizable(false);
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

		img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
/*
		image = new ImageUgly<>(
			img,
			//Function<UglyPixelTYpe,Integer> toRGB, kde UglyPixelType = Integer
			Function.identity()
			,
			//Function<Integer, UglyPixelTYpe> toUgly, kde UglyPixelType = Integer
			Function.identity()
		);
		presenter = new PresenterUgly<>();
/*/
		final Image<Tuple2<Col, Double>> emptyImage =
			new ImageVavr<>(width, height, Tuple.of(black, 1.0));
		image = new ImageBlender<>(emptyImage,
			(newValue, oldValue) ->
//				newValue.apply(
//					(newCol, newDepth) -> oldValue.apply(
//						(oldCol, oldDepth) ->
//							newDepth < oldDepth ? newValue : oldValue
//					)
//				)
				newValue._2 <= oldValue._2 ? newValue : oldValue
		);
		presenter = new PresenterUniversal<>(
				depthPixel -> depthPixel._1.getARGB()
		);
//*/
		final Function3<Tuple2<Col, Double>, Tuple2<Col, Double>, Double, Tuple2<Col, Double>>
				depthPixelLerp =
					(v1, v2, t) -> Tuple.of(
							v1._1.mul(1 - t).add(v2._1.mul(t)),
							v1._2 * (1 - t) + v2._2 * t
					);

		pomImage = image;

		camera = new Camera()
				.withPosition(new Vec3D(5 ,2,2))
				.withAzimuth(Math.PI)
				.withZenith(-Math.atan(2.0 / 5.0));

		mat4 = new Mat4PerspRH(
				Math.PI / 3,
				image.getHeight() / (double) image.getWidth(),
				0.3, 1000
		);


		lineRenderer = new LineRendererDDALerp<>(
				depthPixelLerp
		);

		triangleRenderer = new TriangleRendererScan<>(
				depthPixelLerp
		);


		panel = new JPanel() {
			private static final long serialVersionUID = 1L;

			@Override
			public void paintComponent(Graphics g) {
				super.paintComponent(g);
				present(g);
			}
		};

		renderer = new RenderSolid<Tuple2<Col, Double>, Tuple2<Point3D, Col>>(
				bod -> bod._1 ,
				(vertex, z) -> Tuple.of(new Col(vertex._2), z),
				(v1, v2, t) -> Tuple.of(v1._1.mul(1 - t).add(v2._1.mul(t)),v1._2.mul(1 - t).add(v2._2.mul(t))),
				lineRenderer, triangleRenderer);

		panel.setPreferredSize(new Dimension(width, height));
		panel.requestFocus();
		panel.requestFocusInWindow();






		//ovladani klaves, posunu a meneni vykreslovani teles
		frame.addKeyListener(new KeyAdapter() {

			@Override
			public void keyPressed(KeyEvent o) {


				switch (o.getKeyCode()) {
					case KeyEvent.VK_W:
						camera = camera.forward(0.1);
						draw();
						break;
					case KeyEvent.VK_A:
						camera = camera.left(0.1);
						draw();
						break;
					case KeyEvent.VK_D:
						camera = camera.right(0.1);
						draw();
						break;
					case KeyEvent.VK_S:
						camera = camera.backward(0.1);

						draw();
						break;
					case KeyEvent.VK_Q:
						camera = camera.up(0.1);
						draw();
						break;
					case KeyEvent.VK_E:
						camera = camera.down(0.1);
						draw();
						break;
					case KeyEvent.VK_SPACE:
						posunKostky();
						draw();
						break;
					case KeyEvent.VK_M:
						cableFull();
						draw();
						break;


				}

			}
		});

		//cteni mysi pro rozhlizeni kamery
		panel.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				sC = e.getX();
				sR = e.getY();
				pomPoint2D = new Point2D(sC,sR);
			}
		});
		panel.addMouseMotionListener(new MouseAdapter() {
			@Override
			public void mouseDragged(MouseEvent e) {

				image = pomImage;
				point2D = new Point2D(e.getX(),e.getY());

				mousex = mousex + ((point2D.getX() - pomPoint2D.getX())/360);
				mousey = mousey + ((point2D.getY() - pomPoint2D.getY())/360);

				camera = camera.withAzimuth(Math.PI+mousex);
				camera = camera.withZenith(-Math.atan(2.0 / 5.0)+mousey);

				draw();

				pomPoint2D = new Point2D(e.getX(),e.getY());


			}
		});


		frame.add(panel, BorderLayout.CENTER);
		frame.pack();
		frame.setVisible(true);






	}

	public void clear() {

		image = image.cleared(Tuple.of(black, 1.0));
	}

	public void present(final Graphics graphics) {
//		graphics.drawImage(img, 0, 0, null);
		presenter.present(image, graphics);
	}

	//kreslici trida
	public void draw() {

		//vyprazdneni ozrazce aby na neho mohli byt nakresleny znovu telesa s novou pozici
		image = pomImage;


		final MyPoligom myPoligom = new MyPoligom();
		final Pyramid pyramid = new Pyramid((vec3D.getX()+2.1)/2);
		final Xyz xyz = new Xyz();
		final Cube cube = new Cube(posunKosticky);

		image = renderer.render(image, cube, cube.getParts().get(mody),
								camera
										.move(vec3D)
										.getViewMatrix()
										.mul(mat4),
								Tuple.of(red, 1.0));

		image = renderer.render(image, pyramid, pyramid.getParts().get(mody),
								camera
										.move(new Vec3D(-1,-0.5,0))
										.getViewMatrix()
										.mul(mat4),
								Tuple.of(red, 1.0));

		image = renderer.render(image, xyz, xyz.getParts().get(0),
								camera
										.getViewMatrix()
										.mul(mat4),
								Tuple.of(red, 1.0));

		image =	renderer.render(image, myPoligom,myPoligom.getParts().get(mody),
								camera
										.getViewMatrix()
										.mul(mat4),
								Tuple.of(red, 1.0));

		panel.repaint();

	}

	public void start() {
		draw();
		panel.repaint();
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Canvas(800, 600)::start);
	}

	//trida pro posun teles
	private void posunKostky() {
		posunKosticky = (posunKosticky + 0.1)%(Math.PI*2);
		vec3D = new Vec3D(-1+Math.sin(posunKosticky),-1+Math.sin(posunKosticky),0);
	}

	//trida pro zmenu zobrazeni teles
	private void cableFull() {
		mody = (mody + 1)%2;

	}

}