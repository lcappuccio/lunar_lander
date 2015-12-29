package org.systemexception.lunarlander.main;

import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.*;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.newdawn.slick.Color;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.util.ResourceLoader;

import java.awt.*;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;

import static org.lwjgl.opengl.GL11.*;

/**
 * @author leo
 * @date 29/12/15 14:22
 */
public class TestJbox {

	private static final String WINDOW_TITLE = "Physics in 2D!";
	private static final int[] WINDOW_DIMENSIONS = {800, 600};

	private static final World world = new World(new Vec2(0, 9.8f));
	private static final Set<Body> bodies = new HashSet<Body>();

	private static TrueTypeFont font;

	private static void render() {
		glClear(GL_COLOR_BUFFER_BIT);
		for (Body body : bodies) {
			if (body.getType() == BodyType.DYNAMIC) {
				Color.red.bind();
				glPushMatrix();
				Vec2 bodyPosition = body.getPosition().mul(30);
				glTranslatef(bodyPosition.x, bodyPosition.y, 0);
				glRotated(Math.toDegrees(body.getAngle()), 0, 0, 1);
				glRectf(-0.75f * 30, -0.75f * 30, 0.75f * 30, 0.75f * 30);
				glPopMatrix();
				font.drawString(0, 0, "Position: " + body.getPosition(), org.newdawn.slick.Color.yellow);
			}
		}

	}

	private static void logic() {
		world.step(1 / 60f, 8, 3);
	}

	private static void input() {
		for (Body body : bodies) {
			if (body.getType() == BodyType.DYNAMIC) {
				if (Keyboard.isKeyDown(Keyboard.KEY_A)) {
					body.applyAngularImpulse(-0.005f);
				} else if (Keyboard.isKeyDown(Keyboard.KEY_D)) {
					body.applyAngularImpulse(+0.005f);
				}
				if (Keyboard.isKeyDown(Keyboard.KEY_W)) {
					body.applyForce(new Vec2(0, -4f), body.getPosition());
				}
				if (Mouse.isButtonDown(0)) {
					Vec2 mousePosition = new Vec2(Mouse.getX(), Mouse.getY()).mul(1 / 60f);
					Vec2 bodyPosition = body.getPosition();
					Vec2 force = mousePosition.sub(bodyPosition);
					body.applyForce(force, body.getPosition());
				}
			}
		}
	}

	private static void cleanUp(boolean asCrash) {
		Display.destroy();
		System.exit(asCrash ? 1 : 0);
	}

	private static void setUpMatrices() {
		glMatrixMode(GL_PROJECTION);

		GL11.glShadeModel(GL11.GL_SMOOTH);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glDisable(GL11.GL_LIGHTING);

		GL11.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
		GL11.glClearDepth(1);

		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

		GL11.glViewport(0, 0, Display.getWidth(), Display.getHeight());
		GL11.glMatrixMode(GL11.GL_MODELVIEW);

		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glLoadIdentity();
		GL11.glOrtho(0,  Display.getWidth(), Display.getHeight(), 0, 1, -1);
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
	}

	private static void setUpObjects() {
		BodyDef boxDef = new BodyDef();
		boxDef.position.set(320 / 30 / 2, 240 / 30 / 2);
		boxDef.type = BodyType.DYNAMIC;
		PolygonShape boxShape = new PolygonShape();
		boxShape.setAsBox(0.75f, 0.75f);
		Body box = world.createBody(boxDef);
		FixtureDef boxFixture = new FixtureDef();
		boxFixture.density = 0.1f;
		boxFixture.shape = boxShape;
		boxFixture.restitution = 0.5f;
		box.createFixture(boxFixture);
		bodies.add(box);

		// Bottom Wall
		BodyDef groundDef = new BodyDef();
		groundDef.position.set(0, 20);
		groundDef.type = BodyType.STATIC;
		PolygonShape groundShape = new PolygonShape();
		groundShape.setAsBox(1000, 0);
		Body ground = world.createBody(groundDef);
		FixtureDef groundFixture = new FixtureDef();
		groundFixture.density = 1;
		groundFixture.restitution = 0.0f;
		groundFixture.friction = 5f;
		groundFixture.shape = groundShape;
		ground.createFixture(groundFixture);
		bodies.add(ground);

		// Top Wall
		BodyDef roofDef = new BodyDef();
		roofDef.position.set(0, 0);
		roofDef.type = BodyType.STATIC;
		PolygonShape roofShape = new PolygonShape();
		roofShape.setAsBox(1000, 0);
		Body roof = world.createBody(roofDef);
		FixtureDef roofFixture = new FixtureDef();
		roofFixture.density = 1;
		roofFixture.restitution = 0.0f;
		roofFixture.friction = 5f;
		roofFixture.shape = roofShape;
		roof.createFixture(roofFixture);
		bodies.add(roof);

		// Left Wall
		BodyDef leftWallDef = new BodyDef();
		leftWallDef.position.set(0, 0);
		leftWallDef.type = BodyType.STATIC;
		PolygonShape leftWallShape = new PolygonShape();
		leftWallShape.setAsBox(0, 1000);
		Body leftWall = world.createBody(leftWallDef);
		FixtureDef leftWallFixture = new FixtureDef();
		leftWallFixture.density = 1;
		leftWallFixture.restitution = 0.0f;
		leftWallFixture.friction = 5f;
		leftWallFixture.shape = leftWallShape;
		leftWall.createFixture(leftWallFixture);
		bodies.add(leftWall);

		// Right Wall
		BodyDef rightWallDef = new BodyDef();
		rightWallDef.position.set(27, 0);
		rightWallDef.type = BodyType.STATIC;
		PolygonShape rightWallShape = new PolygonShape();
		rightWallShape.setAsBox(0, 1000);
		Body rightWall = world.createBody(rightWallDef);
		FixtureDef rightWallFixture = new FixtureDef();
		rightWallFixture.density = 1;
		rightWallFixture.restitution = 0.0f;
		rightWallFixture.friction = 5f;
		rightWallFixture.shape = rightWallShape;
		rightWall.createFixture(rightWallFixture);
		bodies.add(rightWall);
	}

	private static void update() {
		Display.update();
		Display.sync(100);
	}

	private static void enterGameLoop() {
		while (!Display.isCloseRequested()) {
			render();
			logic();
			input();
			update();
		}
	}

	private static void setUpDisplay() {
		try {
			Display.setDisplayMode(new DisplayMode(WINDOW_DIMENSIONS[0], WINDOW_DIMENSIONS[1]));
			Display.setTitle(WINDOW_TITLE);
			Display.setVSyncEnabled(true);
			Display.create();
		} catch (LWJGLException e) {
			e.printStackTrace();
			cleanUp(true);
		}

		// load font from file
		try {
			InputStream inputStream = ResourceLoader.getResourceAsStream("ubuntu.ttf");
			Font awtFont2 = Font.createFont(Font.TRUETYPE_FONT, inputStream);
			awtFont2 = awtFont2.deriveFont(24f); // set font size
			font = new TrueTypeFont(awtFont2, true);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		setUpDisplay();
		setUpObjects();
		setUpMatrices();
		enterGameLoop();
		cleanUp(true);
	}
}
