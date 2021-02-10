package br.com.luizmonteiro.snake;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class MainScreen implements Screen {

    private Game game;
    private Viewport viewPort;
    private SpriteBatch batch;
    private Texture[] background;
    private float time;
    private boolean holding;

    public MainScreen(Game game){
        this.game = game;
    }

    @Override
    public void show() {

        batch = new SpriteBatch();

        viewPort = new FillViewport(1000, 1500);
        viewPort.apply();

        background = new Texture[2];
        background[0] = new Texture("fundo0.png");
        background[1] = new Texture("fundo1.png");

        time = 0f;
        holding = false;

        Gdx.input.setInputProcessor(null);

    }

    @Override
    public void render(float delta) {

        time += delta;

        input();

        batch.setProjectionMatrix(viewPort.getCamera().combined);

        Gdx.gl.glClearColor(0.29f, 0.894f, 0.373f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();

        batch.draw(background[(int)time%2], 0, 0, 1000, 1500);

        batch.end();

    }

    private void input() {

        if(Gdx.input.isTouched()){
            holding = true;
        } else if(!Gdx.input.isTouched() && holding){
            holding = false;
            game.setScreen(new GameScreen(game));
        }

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
}
