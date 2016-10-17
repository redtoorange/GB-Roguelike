package com.redtoorange.delver.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.redtoorange.delver.MainGame;
import com.redtoorange.delver.entities.Monster;
import com.redtoorange.delver.entities.Player;
import com.redtoorange.delver.entities.inventory.WeaponFactory;
import com.redtoorange.delver.factories.MonsterFactory;
import com.redtoorange.delver.utility.Constants;
import com.redtoorange.delver.utility.Map.Map;
import com.redtoorange.delver.utility.Map.Tile;


public class PlayingScreen extends GameScreen {
	public boolean loaded = false;
	private MainGame game;
	private Player player;
	private String tileSet = "gbaTileSet.png";
	private SpriteBatch batch;
	private OrthographicCamera camera;
	private Viewport viewport;
	private Map map;
	private Table rootTable;
	private Label hpCount;
	private boolean initialized = false;

	public PlayingScreen(MainGame game){
		this.game = game;
		camera = new OrthographicCamera();
		viewport = new FitViewport( Constants.GB_RES_WIDTH, Constants.GB_RES_HEIGHT, camera);
		initUI( );
		batch = new SpriteBatch();
	}

	protected void initUI() {
		super.initUI();
		//Added for Scene2D.ui
		stage = new Stage( viewport );

		//THIS WILL NOT WORK!!!! Why?
		//stage.setViewport( viewport );

		Label hpLabel = new Label( "HP:", skin, "default" );
		hpCount = new Label( "20", skin, "default" );


		rootTable = new Table( skin );

		rootTable.setSize( Constants.GB_RES_WIDTH, Constants.GB_RES_HEIGHT );
		rootTable.top().left();

		rootTable.setFillParent( true );
		rootTable.setPosition( 0, 0 );

		rootTable.add( hpLabel );
		rootTable.add( hpCount );

		rootTable.row();

		stage.addActor( rootTable );
	}


	@Override
	public void show() {
		if(!initialized){
			initialized = true;
			loadMap( );
		}
		else if(loaded)
			reset();


		//Stage has to be the input processor to handle input from the mouse and such
		Gdx.input.setInputProcessor(stage);
	}

	private void loadMap() {
		loaded = true;
		map = new Map( Gdx.files.internal(tileSet), 20, 20, 0, 0, 32, 32, 16, 16);

		TextureRegion texReg = new TextureRegion(new Texture(Gdx.files.internal(tileSet)), 64, 576, 32, 32);

		Tile spawn = map.getRandomEmptyTile();
		player = new Player(texReg, 16, spawn.getWorldPositionX(), spawn.getWorldPositionY(), map);

		map.addEntity(player);
		spawnBaddies(1);    //Ask factories for stuff

		Tile itemLocation = map.getRandomEmptyTile();
		map.addEntity( WeaponFactory.buildFromJSON("sword", itemLocation) );

		itemLocation = map.getRandomEmptyTile();
		map.addEntity( WeaponFactory.buildFromJSON("axe", itemLocation) );
	}

	@Override
	public void render( float delta ) {
		clearScreen();
		update();
		draw();
	}

	public void reset() {
		player.dispose();
		map.dispose();

		map = new Map(Gdx.files.internal(tileSet), 20, 20, 0, 0, 32, 32, 16, 16);

		TextureRegion texReg = new TextureRegion(new Texture(Gdx.files.internal(tileSet)), 64, 576, 32, 32);
		Tile spawn = map.getRandomEmptyTile();

		player = new Player(texReg, 16, spawn.getWorldPositionX(), spawn.getWorldPositionY(), map);
		map.addEntity(player);
		spawnBaddies(1);    //Ask factories for stuff
	}

	public void resize(int width, int height){
		viewport.update( width, height );
	}

	private void spawnBaddies(int count)    {
		for (int i = 0; i < count; i++)	{
			Tile t = map.getRandomEmptyTile();
			Monster m = MonsterFactory.buildZombie(map, t);
			t.setOccupier(m);
			map.addEntity(m);
		}
	}

	//Place holder, does nothing
	public void mouseClicked(int x, int y)  {
		Vector3 clickPos = new Vector3(x, y, 0);
		clickPos = camera.unproject(clickPos);

		x = MathUtils.round(clickPos.x);
		y = MathUtils.round(clickPos.y);

		Gdx.app.log( "", "Mouse Clicked: " + x + ", " + y );
	}

	private void update()   {
		if (Gdx.input.isKeyJustPressed( Input.Keys.ESCAPE)) {
			game.setScreen(game.getScreenByType(MainGame.ScreenType.PAUSE));
		}
		if(Gdx.input.isKeyJustPressed(Input.Keys.I)){
			game.setScreen(game.getScreenByType(MainGame.ScreenType.INVENTORY));
		}

		if(map != null)
			map.updateables();

		if(camera != null && player != null)
			camera.position.set(player.getPositionX(), player.getPositionY(), 0);

		updateUI( );
	}

	private void updateUI() {
		hpCount.setText( player.getHealth() + "");

		rootTable.setPosition(
				camera.position.x - camera.viewportWidth/2.f,
				camera.position.y - camera.viewportHeight/2.f
		);
		stage.act( Gdx.graphics.getDeltaTime());
	}

	private void draw() {
		camera.update();
		batch.setProjectionMatrix(camera.combined);

		batch.begin();

		if(map != null){
			map.drawMap(batch);
			map.drawables(batch);
		}

		batch.end();

		//The stage has it's own batch to draw, so we must end the other batch before calling the
		//  stage to draw.
		stage.draw();
	}

	public void dispose()   {
		if(player != null)
			player.dispose();

		if(map != null)
			map.dispose();

		if(batch != null)
			batch.dispose();
	}

	public Viewport getViewport(){
		return viewport;
	}
	public Player getPlayer(){
		return player;
	}
}
