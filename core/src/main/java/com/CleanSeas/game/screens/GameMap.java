package com.CleanSeas.game.screens;

//imports da Engine GDX
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Graphics.DisplayMode;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
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
    private Array<Fish> Fs;
    private Random r;// numeros aleatorio para a criacao do lixo;
    private float TD;// tempo onde surge novos lixos;
    private String skin;
    private String skinLixo;

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

    /**Menu pause**/
    private boolean paused = false;
    private Stage pauseStage;
    private BitmapFont pauseFont;

    //constantes
    private final int MAX_TRASH = 20;
    private float minSpawnTime = 1.5f;
    private float maxSpawnTime = 3.0f;
    private float nextSpawnTime = MathUtils.random(minSpawnTime, maxSpawnTime);

    private boolean needsGameOverTransition = false;
    private boolean needsVictoryTransition = false;


    // Classe Button interna
    private class Button {
        float x, y, width, height;
        String text;
        Color color;
        boolean hovered = false;

        public Button(float x, float y, float width, float height, String text, Color color) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.text = text;
            this.color = color;
        }

        public boolean isClicked(float mouseX, float mouseY) {
            return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
        }

        public void updateHover(float mouseX, float mouseY) {
            hovered = isClicked(mouseX, mouseY);
        }
    }

    // Botões do menu pause
    private Button btnContinuar, btnReiniciar, btnSair;

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
        b = new Boat(game.assets.get("pescador.png",Texture.class));
        h = new Hook(game.assets.get("anzol.png",Texture.class), b.getX() + 138, b.getY() - 20);
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

        //Pause
        pauseFont = new BitmapFont();
        pauseFont.getData().setScale(2f);
        pauseStage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(pauseStage);

        // Inicializa os botões do pause
        initPauseButtons();

        for (int i = 0; i < 10; i++) {
            float startX = MathUtils.random(20f, Gdx.graphics.getWidth());
            float startY = MathUtils.random(25f, (Gdx.graphics.getHeight() - 350f));

            switch (r.nextInt(3)){
                case 0:
                    skin = "peixes/anchovy.png";
                    break;
                case 1:
                    skin = "peixes/pufferfish.png";
                    break;
                default:
                    skin = "peixes/surgeonfis.png";
                    break;
            }


            Fs.add(new Fish(game.assets.get(skin, Texture.class), startX, startY));
        }
    }

    private void initPauseButtons() {
        float centerX = Gdx.graphics.getWidth() / 2f;
        float centerY = Gdx.graphics.getHeight() / 2f;
        float buttonWidth = 300;
        float buttonHeight = 60;
        float buttonSpacing = 80;
        float startY = centerY + 80;

        btnContinuar = new Button(centerX - buttonWidth/2, startY, buttonWidth, buttonHeight,
            "Continuar", new Color(0.3f, 0.8f, 0.4f, 1f));
        btnReiniciar = new Button(centerX - buttonWidth/2, startY - buttonSpacing, buttonWidth, buttonHeight,
            "Reiniciar", new Color(1f, 0.7f, 0.2f, 1f));
        btnSair = new Button(centerX - buttonWidth/2, startY - buttonSpacing * 2, buttonWidth, buttonHeight,
            "Sair do Jogo", new Color(0.9f, 0.3f, 0.3f, 1f));
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

        //Pause
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            paused = !paused;
            if (paused) {
                Gdx.input.setInputProcessor(pauseStage);
            } else {
                Gdx.input.setInputProcessor(null);
            }
        }

        if (paused) {
            renderPauseMenu();
            return; // Interrompe o jogo até sair do pause
        }

        timeScore += delta;

        if (Life <= 0) {
            needsGameOverTransition = true;
        }

        if (Score >= 100) {
            needsVictoryTransition = true;
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
        font.draw(game.batch, "Vida: " + (int)Life, hudX +450, hudY + hudHeight);

        spawnTrash(Ts, delta);

        spawnFish(Fs, Hpos, delta);

        b.draw(game.batch);
        h.draw(game.batch);
        game.batch.end();

        shaperenderer.setProjectionMatrix(camera.combined);
        shaperenderer.begin(ShapeRenderer.ShapeType.Line);
        shaperenderer.setColor(Color.WHITE);

        float startX = b.getX() + 138;   // ajuste horizontal
        float startY = b.getY() + 130;   // ajuste vertical

        float endX = h.getX();
        float endY = h.getY() + 20;

        // Desenha a linha
        shaperenderer.line(startX, startY, endX, endY);
        shaperenderer.end();
        shaperenderer.begin(ShapeRenderer.ShapeType.Filled);
        shaperenderer.setColor(Color.BLACK);
        shaperenderer.rectLine(startX, startY, endX, endY, 3f); // largura 4px
        shaperenderer.end();

        DrawBar(hudX, hudY, hudWidth, hudHeight, lifePct);

        if (needsGameOverTransition) {
            Pixmap pixmap = ScreenUtils.getFrameBufferPixmap(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            Texture screenshot = new Texture(pixmap);
            pixmap.dispose();
            game.setScreen(new GameOverScreen(game, Score, (int)timeScore, screenshot));
            dispose();
            return;
        }

        if (needsVictoryTransition) {
            Pixmap pixmap = ScreenUtils.getFrameBufferPixmap(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            Texture screenshot = new Texture(pixmap);
            pixmap.dispose();
            game.setScreen(new VictoryScreen(game, Score, (int)timeScore, screenshot));
            dispose();
            return;
        }
    }

    @Override
    public void resize(int width, int height) {//chamando quando a janela muda de tamanho
        viewport.update(width, height, true);
        hudCamera.setToOrtho(false, width, height);
        hudCamera.update();
        // Reinicializa os botões com as novas dimensões
        initPauseButtons();
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
            f.update(delta, c,  viewport, Hpos);
        }
        for (Fish f : Fs) {
            if (f.canDamage() && f.getBounds().overlaps(h.getBounds())) {
                Life -= 25;
                f.markHit();
            }
        }
        for (Fish f : Fs) {
            f.draw(game.batch);
        }
    }

    private void spawnTrash(Array<Trash> Ts, float delta) {
        TD += delta;
        if (TD > nextSpawnTime && Ts.size < MAX_TRASH) {
            TD = 0;
            nextSpawnTime = MathUtils.random(minSpawnTime, maxSpawnTime);
            int camada = r.nextInt(3);

            switch (r.nextInt(2)){
                case 0:
                    skinLixo = "trash/rusty.png";
                    break;
                default:
                    skinLixo = "trash/worm.png";
                    break;
            }

            //escolher aleatoriamente a camada onde se surege o lixo
            Ts.add(new Trash(game.assets.get(skinLixo, Texture.class), r.nextFloat() * (Gdx.graphics.getWidth() - 10), YTrash(camada), camada));
        }
        for (Iterator<Trash> it = Ts.iterator(); it.hasNext();) {
            Trash t = it.next();
            t.update(delta, c);
            t.draw(game.batch);
            //remove se saiu da tela
            if (h.getBounds().overlaps(t.getBounds())) {
                Score += t.depth.points;  // Agora modifica a variável da classe diretamente
                it.remove();
            }
            if (t.ScreenOut(Gdx.graphics.getWidth(), Gdx.graphics.getHeight())) {
                it.remove();
            }
        }
    }

    private float YTrash(int camada){
        float y;
        switch (camada) {
            case 0:
                y = viewport.getWorldHeight() - 350;
                break;
            case 1:
                y = viewport.getWorldWidth() / 2f - 150;
                break;
            default :
                y = 25;
                break;
        }
        return y;
    }

    private void renderPauseMenu() {
        float panelWidth = 450;
        float panelHeight = 450;

        float centerX = Gdx.graphics.getWidth() / 2f;
        float centerY = Gdx.graphics.getHeight() / 2f;

        float panelX = centerX - panelWidth / 2f;
        float panelY = centerY - panelHeight / 2f;

        // Posição do mouse
        float mouseX = Gdx.input.getX();
        float mouseY = Gdx.graphics.getHeight() - Gdx.input.getY();

        shaperenderer.setProjectionMatrix(hudCamera.combined);

        // Painel principal
        shaperenderer.begin(ShapeRenderer.ShapeType.Filled);
        shaperenderer.setColor(0.15f, 0.15f, 0.2f, 0.98f);
        shaperenderer.rect(panelX, panelY, panelWidth, panelHeight);
        shaperenderer.end();

        // Barra superior
        shaperenderer.begin(ShapeRenderer.ShapeType.Filled);
        shaperenderer.setColor(0.2f, 0.6f, 0.85f, 1f);
        shaperenderer.rect(panelX, panelY + panelHeight - 70, panelWidth, 70);
        shaperenderer.end();

        // Desenha os botões
        Button[] buttons = {btnContinuar, btnReiniciar, btnSair};
        for (Button btn : buttons) {
            btn.updateHover(mouseX, mouseY);

            shaperenderer.begin(ShapeRenderer.ShapeType.Filled);
            if (btn.hovered) {
                shaperenderer.setColor(btn.color.r * 1.2f, btn.color.g * 1.2f, btn.color.b * 1.2f, 1f);
            } else {
                shaperenderer.setColor(btn.color);
            }
            shaperenderer.rect(btn.x, btn.y, btn.width, btn.height);
            shaperenderer.end();

            // Borda do botão
            shaperenderer.begin(ShapeRenderer.ShapeType.Line);
            Gdx.gl.glLineWidth(2);
            if (btn.hovered) {
                shaperenderer.setColor(Color.WHITE);
            } else {
                shaperenderer.setColor(0.4f, 0.4f, 0.45f, 1f);
            }
            shaperenderer.rect(btn.x, btn.y, btn.width, btn.height);
            Gdx.gl.glLineWidth(1);
            shaperenderer.end();
        }

        game.batch.setProjectionMatrix(hudCamera.combined);
        game.batch.begin();

        // Título
        pauseFont.getData().setScale(2.5f);
        pauseFont.setColor(Color.WHITE);
        String title = "PAUSA";
        pauseFont.draw(game.batch, title, centerX - 70, panelY + panelHeight - 25);

        font.getData().setScale(1.8f);
        font.setColor(Color.WHITE);
        GlyphLayout layout = new GlyphLayout();
        for (Button btn : buttons) {
            layout.setText(font, btn.text);
            float textWidth = layout.width;
            float textHeight = layout.height;
            font.draw(game.batch, btn.text,
                btn.x + btn.width / 2 - textWidth / 2,
                btn.y + btn.height / 2 + textHeight / 2);
        }

        font.getData().setScale(1.2f);
        pauseFont.getData().setScale(2f);
        game.batch.end();

        // Clique nos botões
        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            if (btnContinuar.isClicked(mouseX, mouseY)) {
                paused = false;
                Gdx.input.setInputProcessor(null);
            } else if (btnReiniciar.isClicked(mouseX, mouseY)) {
                game.setScreen(new GameMap(game));
            } else if (btnSair.isClicked(mouseX, mouseY)) {
                Gdx.app.exit();
            }
        }
    }


    @Override
    public void hide(){
        //esconde a tela para ir pra proxima
    }

    @Override
    public void dispose(){
        //Libera os recursos
        font.dispose();
        shaperenderer.dispose();
        pauseFont.dispose();
    }
}
