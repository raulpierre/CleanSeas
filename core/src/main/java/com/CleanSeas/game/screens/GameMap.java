package com.CleanSeas.game.screens;

//imports da Engine GDX
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Graphics.DisplayMode;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.Pool;
//imports do meu jogo
import com.CleanSeas.game.CleanSeas;
import com.CleanSeas.game.entities.Boat;
import com.CleanSeas.game.entities.Hook;
import com.CleanSeas.game.entities.Trash;
import com.CleanSeas.game.entities.OceanCurrent;
import com.CleanSeas.game.entities.Fish;
//import JAVA padrao
import java.util.Random;
import java.util.Iterator;
import java.util.ArrayList;

public class GameMap implements Screen{
    private final CleanSeas game;//referencia ao Game, para o momento de troca de tela
    private Boat b;
    private Hook h;
    private OceanCurrent c;
    private Array<Trash> Ts;//array de lixo;
    private Pool<Trash> trashPool = new Pool<Trash>(){
            @Override
            protected Trash newObject() {
                return new Trash();
            }
    };
    private Array<Fish> Fs;
    private Random r;// numeros aleatorio para a criacao do lixo;
    private float TD;// tempo onde surge novos lixos;

    /**HUD*/
    private BitmapFont font;
    private ShapeRenderer shaperenderer;
    private int Score = 0;
    private float Life = 100f;
    private float timeScore = 0;

    /** variaveis de configuracao da camera*/
    private OrthographicCamera camera;
    private Viewport viewport;
    private Texture map;
    private OrthographicCamera hudCamera;

    //constantes
    private final int MAX_TRASH = 20;
    private float minSpawnTime = 1.5f;
    private float maxSpawnTime = 3.0f;
    private float nextSpawnTime = MathUtils.random(minSpawnTime, maxSpawnTime);

    public GameMap(CleanSeas game){
        this.game = game;// guarda a referencia
    }

    @Override
    public void show(){
        //configuracao da camera
        camera = new OrthographicCamera();
        viewport = new StretchViewport(1280, 720, camera);
        viewport.apply();
        //Carrega as imagem
        map = game.assets.get("gamemap.png",Texture.class);
        b = new Boat(game.assets.get("img_testes.png",Texture.class));
        h = new Hook(game.assets.get("anzol_testes.png",Texture.class), b.getX(), b.getY() - 20);
        Ts = new Array<>();
        Fs = new Array<>();
        r = new Random();
        c = new OceanCurrent(30f, 30);
        TD = 0;

        //HUD
        font = new BitmapFont();
        font.getData().setScale(1.2f);
        shaperenderer = new ShapeRenderer();
        hudCamera = new OrthographicCamera();
        hudCamera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        hudCamera.update();

        for (int i = 0; i < 10; i++) {
            float startX = MathUtils.random(20f, Gdx.graphics.getWidth());
            float startY = MathUtils.random(25f, (Gdx.graphics.getHeight() - 350f));

            Fs.add(new Fish(game.assets.get("Clownfish.png", Texture.class), startX, startY));
        }
    }

    @Override
    public void render(float delta) {
        /**configurao tela*/
        camera.update();
        game.batch.setProjectionMatrix(camera.combined);

        if(Gdx.input.isKeyJustPressed(Input.Keys.F11)){
            //volta para o modo janela
            if(Gdx.graphics.isFullscreen()){
                Gdx.graphics.setWindowedMode(1280, 720);
            }

            else{
                Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
            }
            viewport.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
        }

        /**HUD*/


        //delta = tempo em segundos desde o ultimo frame
        b.update(delta);
        h.update(delta, b.getX(), b.getY());

        /**Verifica se o residuo Bateu no Anzol e o Atualiza*/
        Vector2 Hpos = new Vector2(h.getX(), h.getY());

        /**geracao de residuos*/


        float hudX = 20f;
        float hudY = viewport.getWorldHeight() - 30;
        float hudWidth = 220;
        float hudHeight = 16;
        float lifePct = Math.max(0f, Math.min(1f, Life / 100f));

        /**Limpa e Desenha a tela */
        //Limpa a tela
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);


        game.batch.begin();
        game.batch.draw(map, 0, 0, viewport.getWorldWidth(), viewport.getWorldHeight());


        /**HUD*/

        font.setColor(Color.WHITE);
        font.draw(game.batch, "Score: " + Score, hudX, hudY + hudHeight);
        font.draw(game.batch, "Tempo: " + (int)timeScore, hudX + 250, hudY + hudHeight);
        font.draw(game.batch, "Vida: " + (int)Life, hudX , hudY - hudHeight);



        spawnTrash(Ts, delta);

        spawnFish(Fs, Hpos, delta);

        b.draw(game.batch);
        h.draw(game.batch);
        game.batch.end();

        DrawBar(hudX, hudY, hudWidth, hudHeight, lifePct);
    }
    @Override
    public void resize(int width, int height) {//chamando quando a janela muda de tamanho
        viewport.update(width, height, true);
    }

    @Override public void pause () {
    }
    @Override public void resume () {
    }

    private void DrawBar(float hudX, float hudY, float hudWidth, float hudHeight, float lifePct) {
        shaperenderer.setProjectionMatrix(hudCamera.combined);

        shaperenderer.begin(ShapeRenderer.ShapeType.Filled);
        /**Desenha a parte atras da vida*/
        shaperenderer.setColor(Color.DARK_GRAY);
        shaperenderer.rect(hudX, hudY, hudWidth, hudHeight);
        /**Desenha a vida*/

        shaperenderer.setColor(Color.SCARLET);
        shaperenderer.rect(hudX, hudY, hudWidth * lifePct, hudHeight);
        shaperenderer.end();
    }

    private void spawnFish(Array<Fish> Fs, Vector2 Hpos, float delta){
        for(Fish f : Fs) {
            f.update(delta, viewport, Hpos);
        }
        for (Fish f : Fs) {
            if (f.canDamage() && h.getBounds().overlaps(f.getBounds())) {
                Life -= 25;
                f.markHit();
            }
        }
        for (Fish f : Fs) {
            f.draw(game.batch);
        }
    }

    private void spawnTrash(Array<Trash> Ts,  float delta) {
        TD += delta;
        if (TD > nextSpawnTime && Ts.size < MAX_TRASH) {
            TD = 0;
            nextSpawnTime = MathUtils.random(minSpawnTime, maxSpawnTime);
            Trash t =  trashPool.obtain();
            int camada = r.nextInt(3);
            //escolher aleatoriamente a camada onde se surege o lixo
            t.init(game.assets.get("garrafapet.png", Texture.class), r.nextFloat() * (Gdx.graphics.getWidth() - 10), YTrash(camada), camada);
            Ts.add(t);
        }
        for (int i = Ts.size - 1; i >= 0; i--) {
            Trash t = Ts.get(i);
            t.update(delta, c);
            if (h.getBounds().overlaps(t.getBounds())) {
                Score += t.depth.points;
                Ts.removeIndex(i);
                trashPool.free(t);
                continue;
            }
            if (t.ScreenOut(Gdx.graphics.getWidth(), Gdx.graphics.getHeight())) {
                Ts.removeIndex(i);
                trashPool.free(t);
                continue;
            }
            t.draw(game.batch);
        }
    }
    private float YTrash(int camada){
        float y;
        switch (camada) {
            case 0:
                y = b.getY();
                break;
            case 1:
                y = b.getY() - 200;
                break;
            default :
                y = 25;
                break;
        }
        return y;
    }

    @Override
    public void hide(){
        //esconde a tela para ir pra proxima
    }
    @Override
    public void dispose(){
        //Libera os recur
        font.dispose();
        shaperenderer.dispose();
    }
}
