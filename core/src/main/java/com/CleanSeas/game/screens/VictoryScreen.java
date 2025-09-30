package com.CleanSeas.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.Input;
import com.CleanSeas.game.CleanSeas;

public class VictoryScreen implements Screen {
    private final CleanSeas game;
    private final int finalScore;
    private final int finalTime;

    private OrthographicCamera camera;
    private BitmapFont titleFont;
    private BitmapFont textFont;
    private ShapeRenderer shapeRenderer;

    private Texture backgroundTexture;

    private Button btnReiniciar, btnSair;

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

    public VictoryScreen(CleanSeas game, int score, int time, Texture mapTexture) {
        this.game = game;
        this.finalScore = score;
        this.finalTime = time;
        this.backgroundTexture = mapTexture;
    }

    @Override
    public void show() {
        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        titleFont = new BitmapFont();
        titleFont.getData().setScale(4f);

        textFont = new BitmapFont();
        textFont.getData().setScale(2f);

        shapeRenderer = new ShapeRenderer();

        initButtons();
    }

    private void initButtons() {
        float centerX = Gdx.graphics.getWidth() / 2f;
        float centerY = Gdx.graphics.getHeight() / 2f;
        float buttonWidth = 300;
        float buttonHeight = 60;
        float buttonSpacing = 80;
        float startY = centerY - 100;

        btnReiniciar = new Button(centerX - buttonWidth/2, startY, buttonWidth, buttonHeight,
            "Jogar Novamente", new Color(1f, 0.7f, 0.2f, 1f));
        btnSair = new Button(centerX - buttonWidth/2, startY - buttonSpacing, buttonWidth, buttonHeight,
            "Sair do Jogo", new Color(0.9f, 0.3f, 0.3f, 1f));
    }

    @Override
    public void render(float delta) {
        // Garante que o batch está fechado
        if (game.batch.isDrawing()) {
            game.batch.end();
        }

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();

        float centerX = Gdx.graphics.getWidth() / 2f;
        float centerY = Gdx.graphics.getHeight() / 2f;

        float panelWidth = 500;
        float panelHeight = 550;
        float panelX = centerX - panelWidth / 2f;
        float panelY = centerY - panelHeight / 2f;

        // Posição do mouse
        float mouseX = Gdx.input.getX();
        float mouseY = Gdx.graphics.getHeight() - Gdx.input.getY();

        game.batch.setProjectionMatrix(camera.combined);
        game.batch.begin();

        // Desenha o mapa do jogo como fundo
        game.batch.draw(backgroundTexture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());


        game.batch.end();

        // Overlay escuro semi-transparente
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0f, 0f, 0f, 0.5f);
        shapeRenderer.rect(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        shapeRenderer.end();

        // Painel principal
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0.15f, 0.2f, 0.15f, 0.95f);
        shapeRenderer.rect(panelX, panelY, panelWidth, panelHeight);
        shapeRenderer.end();

        // Barra superior verde
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0.2f, 0.7f, 0.3f, 1f);
        shapeRenderer.rect(panelX, panelY + panelHeight - 80, panelWidth, 80);
        shapeRenderer.end();

        // Desenha os botões
        Button[] buttons = {btnReiniciar, btnSair};
        for (Button btn : buttons) {
            btn.updateHover(mouseX, mouseY);

            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            if (btn.hovered) {
                shapeRenderer.setColor(btn.color.r * 1.2f, btn.color.g * 1.2f, btn.color.b * 1.2f, 1f);
            } else {
                shapeRenderer.setColor(btn.color);
            }
            shapeRenderer.rect(btn.x, btn.y, btn.width, btn.height);
            shapeRenderer.end();

            // Borda do botão
            shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            Gdx.gl.glLineWidth(2);
            if (btn.hovered) {
                shapeRenderer.setColor(Color.WHITE);
            } else {
                shapeRenderer.setColor(0.4f, 0.4f, 0.45f, 1f);
            }
            shapeRenderer.rect(btn.x, btn.y, btn.width, btn.height);
            Gdx.gl.glLineWidth(1);
            shapeRenderer.end();
        }

        game.batch.setProjectionMatrix(camera.combined);
        game.batch.begin();

        // Título VITÓRIA
        titleFont.setColor(Color.GOLD);
        GlyphLayout titleLayout = new GlyphLayout(titleFont, "VITORIA!");
        titleFont.draw(game.batch, "VITORIA!",
            centerX - titleLayout.width / 2,
            panelY + panelHeight - 25);

        // Mensagem de parabéns
        textFont.getData().setScale(1.5f);
        textFont.setColor(Color.WHITE);
        String congrats = "Parabens!";
        String message = "Voce limpou os oceanos!";

        GlyphLayout congratsLayout = new GlyphLayout(textFont, congrats);
        GlyphLayout messageLayout = new GlyphLayout(textFont, message);

        textFont.draw(game.batch, congrats,
            centerX - congratsLayout.width / 2,
            centerY + 160);
        textFont.draw(game.batch, message,
            centerX - messageLayout.width / 2,
            centerY + 125);

        // Estatísticas
        textFont.getData().setScale(2f);
        String scoreText = "Pontuacao: " + finalScore;
        String timeText = "Tempo: " + finalTime + "s";

        GlyphLayout scoreLayout = new GlyphLayout(textFont, scoreText);
        GlyphLayout timeLayout = new GlyphLayout(textFont, timeText);

        textFont.draw(game.batch, scoreText,
            centerX - scoreLayout.width / 2,
            centerY + 70);
        textFont.draw(game.batch, timeText,
            centerX - timeLayout.width / 2,
            centerY + 30);

        // Texto dos botões
        textFont.getData().setScale(1.8f);
        GlyphLayout layout = new GlyphLayout();
        for (Button btn : buttons) {
            layout.setText(textFont, btn.text);
            float textWidth = layout.width;
            float textHeight = layout.height;
            textFont.draw(game.batch, btn.text,
                btn.x + btn.width / 2 - textWidth / 2,
                btn.y + btn.height / 2 + textHeight / 2);
        }

        textFont.getData().setScale(2f);
        game.batch.end();

        // Clique nos botões
        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            if (btnReiniciar.isClicked(mouseX, mouseY)) {
                game.setScreen(new GameMap(game));
                dispose();
            } else if (btnSair.isClicked(mouseX, mouseY)) {
                Gdx.app.exit();
            }
        }
    }

    @Override
    public void resize(int width, int height) {
        camera.setToOrtho(false, width, height);
        camera.update();
        initButtons();
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}

    @Override
    public void dispose() {
        titleFont.dispose();
        textFont.dispose();
        shapeRenderer.dispose();
        // Não dispose da backgroundTexture aqui pois ela é o screenshot
    }
}
