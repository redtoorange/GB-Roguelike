package com.redtoorange.delver;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.redtoorange.delver.entities.Monster;
import com.redtoorange.delver.entities.Player;
import com.redtoorange.delver.factories.MonsterFactory;
import com.redtoorange.delver.utility.Constants;

public class MainGame extends ApplicationAdapter{
    private Player player;
    private String tileSet = "gbaTileSet.png";
    private Color clearColor = new Color(0, 0, 0, 0);
    private SpriteBatch batch;
    private OrthographicCamera camera;
    private Viewport viewport;

    private Map map;
    private InputManager input;

    public void create()
    {
        input = new InputManager(this);
        Gdx.input.setInputProcessor(input);

        camera = new OrthographicCamera();

        //Regardless of the resolition of the window, force it to fit this...
        viewport = new FitViewport(Constants.GB_RES_WIDTH, Constants.GB_RES_HEIGHT, camera);


        batch = new SpriteBatch();
        map = new Map(Gdx.files.internal(tileSet), 20, 20, 0, 0, 32, 32, 16, 16);

        TextureRegion texReg = new TextureRegion(new Texture(Gdx.files.internal(tileSet)), 64, 576, 32, 32);


        player = new Player(texReg, 16, 32, 32, map);


        map.addCharacter(player);
        spawnBaddies(1);    //Ask factories for stuff
    }

    public void resize(int width, int height){
        viewport.update( width, height );
    }

    private void spawnBaddies(int count)
    {
        for (int i = 0; i < count; i++)
        {
            Tile t = map.getRandomEmptyTile();
            Monster m = MonsterFactory.buildZombie(map, t);
            t.setOccupier(m);
            map.addCharacter(m);
        }
    }

    //Place holder, does nothing
    public void mouseClicked(int x, int y)
    {
        Vector3 clickPos = new Vector3(x, y, 0);
        clickPos = camera.unproject(clickPos);

        x = MathUtils.round(clickPos.x);
        y = MathUtils.round(clickPos.y);
    }

    public void render()
    {
        update();
        clearScreen();
        draw();
    }

    private void update()
    {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE))
            Gdx.app.exit();

        map.updateCharacters();

        camera.position.set(player.getPositionX(), player.getPositionY(), 0);
    }

    private void draw()
    {
        camera.update();
        batch.setProjectionMatrix(camera.combined);

        batch.begin();

        map.drawMap(batch);
        map.drawCharacters(batch);

        batch.end();
    }

    private void clearScreen()
    {
        Gdx.gl.glClearColor(clearColor.r, clearColor.g, clearColor.b, clearColor.a);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    }

    public void dispose()
    {
        player.dispose();
        map.dispose();
        batch.dispose();
    }
}