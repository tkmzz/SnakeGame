package br.com.luizmonteiro.snake;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.Random;

public class GameScreen implements Screen, GestureDetector.GestureListener {

    private Game game;

    private Viewport viewPort;
    private SpriteBatch batch;

    private Texture bodyTexture;
    private Texture backgroundTexture;
    private Texture scoreTexture;

    private boolean[][] body;
    private Array<Vector2> bodyParts;
    private int bodyDirection;
    private float timeToMove;
    private Vector2 touch;
    private Array<Vector2> points;
    private float timeToNext;
    private Random random;
    private int status;

    public GameScreen(Game game){
        this.game = game;
    }

    @Override
    public void show() {

        batch = new SpriteBatch();
        viewPort = new FitViewport(100, 100);
        viewPort.apply();

        generateTexture();

        init();

        Gdx.input.setInputProcessor(new GestureDetector(this));
        touch = new Vector2();
        random = new Random();

    }

    private void generateTexture() {

        Pixmap snakeBodyMap = new Pixmap(64, 64, Pixmap.Format.RGBA8888);
        snakeBodyMap.setColor(1f, 1f, 1f, 1f);
        snakeBodyMap.fillRectangle(0, 0, 64, 64);
        bodyTexture = new Texture(snakeBodyMap);
        snakeBodyMap.dispose();

        Pixmap backgroundMap = new Pixmap(64, 64, Pixmap.Format.RGBA8888);
        backgroundMap.setColor(0.29f, 0.784f, 0.373f, 0.5f);
        backgroundMap.fillRectangle(0, 0, 64, 64);
        backgroundTexture = new Texture(backgroundMap);
        backgroundMap.dispose();

        Pixmap scoreMap = new Pixmap(64, 64, Pixmap.Format.RGBA8888);
        scoreMap.setColor(1f, 1f, 1f, 1f);
        scoreMap.fillCircle(32, 32, 32);
        scoreTexture = new Texture(scoreMap);
        scoreMap.dispose();
    }

    private void init() {
        body = new boolean[20][20];
        bodyParts = new Array<Vector2>();

        bodyParts.add(new Vector2(6, 5));
        body[6][5] = true;

        bodyParts.add(new Vector2(5, 5));
        body[5][5] = true;

        bodyDirection = 2;
        timeToMove = 0.4f;
        points = new Array<Vector2>();
        timeToNext = 3f;
        status = 0;
    }

    @Override
    public void render(float delta) {
        update(delta);

        batch.setProjectionMatrix(viewPort.getCamera().combined);
        Gdx.gl.glClearColor(0.29f, 0.894f, 0.373f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();

        batch.draw(backgroundTexture, 0, 0, 100, 100);

        for(Vector2 bodyPart: bodyParts){
            batch.draw(bodyTexture, bodyPart.x*5, bodyPart.y*5, 5, 5);
        }

        for(Vector2 point: points){
            batch.draw(scoreTexture, point.x*5, point.y*5, 5, 5);
        }

        batch.end();

    }

    private void update(float delta) {
        if(status == 0){
            timeToMove -= delta;
            if (timeToMove <= 0){
                timeToMove = 0.4f;

                int x1, x2, y1, y2;

                x1 = (int) bodyParts.get(0).x;
                y1 = (int) bodyParts.get(0).y;
                body[x1][y1] = false;

                x2 = x1;
                y2 = y1;

                switch (bodyDirection){
                    case 1:
                        y1++;
                        break;
                    case 2:
                        x1++;
                        break;
                    case 3:
                        y1--;
                        break;
                    case 4:
                        x1--;
                        break;
                }

                if(x1 < 0 || y1 < 0 || x1 > 19 || y1 > 19 || body[x1][y1]){
                    status = 1;
                    return;
                }

                for(int j=0; j < points.size ; j++){
                    if(points.get(j).x == x1 && points.get(j).y == y1){
                        points.removeIndex(j);
                        bodyParts.insert(0, new Vector2(x1, y1));
                        body[x1][y1] = true;
                        body[x2][y2] = true;
                        return;
                    }
                }

                bodyParts.get(0).set(x1,y1);
                body[x1][y1] = true;

                for(int i = 1; i < bodyParts.size; i++){
                    x1 = (int) bodyParts.get(i).x;
                    y1 = (int) bodyParts.get(i).y;
                    body[x1][y1] = false;

                    bodyParts.get(i).set(x2, y2);
                    body[x2][y2] = true;

                    x2 = x1;
                    y2= y1;
                }
            }

            timeToNext -= delta;
            if(timeToNext <= 0){
                int x = random.nextInt(20);
                int y = random.nextInt(20);
                if(!body[x][y]){
                    points.add(new Vector2(x,y));
                    timeToNext = 5f;
                }
            }
        }
    }

    @Override
    public boolean fling(float velocityX, float velocityY, int button) {
        viewPort.unproject(touch.set(velocityX, velocityY));

        if(status == 0){
            if(Math.abs(touch.x) > Math.abs(touch.y)) touch.y = 0;
            else touch.x = 0;

            if(touch.x > 50 && bodyDirection != 4){
                bodyDirection = 2;
            } else if(touch.y > 50 && bodyDirection != 3){
                bodyDirection = 1;
            } else if (touch.x < -50 && bodyDirection != 2){
                bodyDirection = 4;
            } else if (touch.y < -50 && bodyDirection != 1){
                bodyDirection = 3;
            }
        }
        return true;
    }

    @Override
    public boolean tap(float x, float y, int count, int button) {
        if(status == 1) game.setScreen(new MainScreen(game));
        return true;
    }

    @Override
    public void resize(int width, int height) {
        viewPort.update(width, height, true);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }

    @Override
    public boolean touchDown(float x, float y, int pointer, int button) {
        return false;
    }

    @Override
    public boolean longPress(float x, float y) {
        return false;
    }

    @Override
    public boolean pan(float x, float y, float deltaX, float deltaY) {
        return false;
    }

    @Override
    public boolean panStop(float x, float y, int pointer, int button) {
        return false;
    }

    @Override
    public boolean zoom(float initialDistance, float distance) {
        return false;
    }

    @Override
    public boolean pinch(Vector2 initialPointer1, Vector2 initialPointer2, Vector2 pointer1, Vector2 pointer2) {
        return false;
    }

    @Override
    public void pinchStop() {

    }
}
