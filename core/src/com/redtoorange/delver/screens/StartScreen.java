package com.redtoorange.delver.screens;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.redtoorange.delver.MainGame;
import com.redtoorange.delver.utility.Constants;

public class StartScreen extends GameScreen {
    private MainGame game;
    private PlayingScreen playingScreen;
    private Table rootTable;
    private TextButton startButton;
    private TextButton exitButton;
    private boolean initialized = false;

    public StartScreen(MainGame game){
        this.game = game;
        initUI( );
    }

    protected void initUI( ) {
        super.initUI();

        rootTable = new Table(skin);
        stage.addActor( rootTable );

        rootTable.setSize( Constants.GB_RES_WIDTH, Constants.GB_RES_HEIGHT );

        rootTable.debug();

        startButton = new TextButton("Start", skin, "default");
        startButton.setProgrammaticChangeEvents(false);
        startButton.addListener( new ChangeScreenEvent(game, MainGame.ScreenType.PLAYING));
        rootTable.add(startButton).width(80);

        rootTable.row();

        exitButton = new TextButton("Exit", skin, "default");
        exitButton.setProgrammaticChangeEvents(false);
        exitButton.addListener( new ExitButtonListener());
        rootTable.add(exitButton).width(80);
    }

    class ExitButtonListener extends ChangeListener {
        @Override
        public void changed(ChangeEvent event, Actor actor) {
            Gdx.app.exit();
        }
    }

    @Override
    public void show() {
        if(!initialized){
            initialized = true;

            playingScreen = (PlayingScreen)game.getScreenByType(MainGame.ScreenType.PLAYING);
            stage.setViewport(playingScreen.getViewport());
        }

        realignMainTable();
    }

    private void realignMainTable() {
        Gdx.input.setInputProcessor(stage);

        rootTable.setPosition(
                stage.getCamera().position.x - rootTable.getWidth()/2,
                stage.getCamera().position.y - rootTable.getHeight()/2
        );
    }

    @Override
    public void render(float delta) {
        update(delta);
        draw();

        System.out.println( "X:" + rootTable.getX() + " Y:" + rootTable.getY() );
        System.out.println("Mouse X:" + Gdx.input.getX() + " Y:" + Gdx.input.getY()  );
    }

    protected void update(float delta){
        stage.act(delta);
    }

    protected void draw(){
        clearScreen();
        stage.draw();
    }
}
