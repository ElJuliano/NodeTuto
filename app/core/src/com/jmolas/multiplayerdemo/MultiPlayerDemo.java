package com.jmolas.multiplayerdemo;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.jmolas.multiplayerdemo.sprites.SpaceShip;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;


public class MultiPlayerDemo extends ApplicationAdapter {

    private final float UPDATE_TIME = 1/60f;
    float timer;
	private Socket socket;
	SpriteBatch batch;
	Texture img;
    String id;
    SpaceShip player;
    Texture playerShip;
    Texture friendlyShip;
    HashMap<String, SpaceShip> friendlyPlayer;
	
	@Override
	public void create () {
		batch = new SpriteBatch();
		img = new Texture("badlogic.jpg");
        playerShip = new Texture("spaceship.png");
        friendlyShip = new Texture("spaceship2.png");
        friendlyPlayer = new HashMap<String, SpaceShip>();
		connectSocket();
        configSocketEvents();
	}

    public void updateServer(float dt) {
        timer += dt;
        if(timer >= UPDATE_TIME && player != null && player.hasMoved()) {
            JSONObject data = new JSONObject();
            try {
                data.put("x", player.getX());
                data.put("y", player.getY());
                socket.emit("PLAYER_MOVED", data);
            }
            catch(JSONException e) {
                Gdx.app.log("SOCKET.IO", "Error sending the data");
            }
        }
    }

	@Override
	public void render () {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        handleInput(Gdx.graphics.getDeltaTime());
        updateServer(Gdx.graphics.getDeltaTime());

		batch.begin();
		if(player != null) {
            player.draw(batch);
        }
        for(HashMap.Entry<String, SpaceShip> entry : friendlyPlayer.entrySet()) {
            entry.getValue().draw(batch);
        }
		batch.end();
	}

    private void handleInput (float dt) {
        if(player!= null) {
            if(Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
                player.setPosition(player.getX() + (-200)* dt, player.getY());
            }
            else if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
                player.setPosition(player.getX() + 200 * dt, player.getY());
            }
            else if(Gdx.input.isKeyPressed(Input.Keys.UP)) {
                player.setPosition(player.getX(), player.getY() + 200 * dt);
            }
            else if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
                player.setPosition(player.getX(), player.getY() - 200 * dt);
            }
        }
    }
	
	@Override
	public void dispose () {
        super.dispose();
		batch.dispose();
		img.dispose();
        playerShip.dispose();
        friendlyShip.dispose();
	}
	public void connectSocket(){
		try{
			socket = IO.socket("http://localhost:8080");
			socket.connect();
		}
		catch(Exception e) {
			System.err.println("[Socket exception] : "+e.getMessage());
		}
	}

    public void configSocketEvents() {
        socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Gdx.app.log("SocketIO", "Connected");
                player = new SpaceShip(playerShip);
            }
        }).on("SOCKET_ID", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                try {
                    JSONObject data = (JSONObject) args[0];
                    String id = data.getString("id");
                    Gdx.app.log("SocketIO", "Server Socket id : " + id);
                }
                catch (JSONException e) {
                    Gdx.app.log("SocketIO", "Error getting id");
                }

            }
        }).on("NEW_PLAYER", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                try {
                    JSONObject data = (JSONObject) args[0];
                    String playerId = data.getString("id");
                    Gdx.app.log("SocketIO", "New player id : " + playerId);
                    SpaceShip sp = new SpaceShip(friendlyShip, true);
                    sp.setPosition(0, 300);
                    friendlyPlayer.put(playerId, sp);
                }
                catch (JSONException e) {
                    Gdx.app.log("SocketIO", "Error getting new player id");
                }

            }
        }).on("PLAYER_DISCONNECTED", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                try {
                    JSONObject data = (JSONObject) args[0];
                    String id = data.getString("id");
                    friendlyPlayer.remove(id);
                }
                catch (JSONException e) {
                    Gdx.app.log("SocketIO", "Error getting new player id");
                }

            }
        }).on("PLAYER_MOVED", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                JSONObject data = (JSONObject) args[0];
                try {
                    String playerId  = data.getString("id");
                    Double x = data.getDouble("x");
                    Double y = data.getDouble("y");
                    if(friendlyPlayer.get(playerId)!= null) {
                        friendlyPlayer.get(playerId).setPosition(x.floatValue(), y.floatValue()+300);
                    }
                }
                catch (JSONException e) {
                    Gdx.app.log("SocketIO", "Error player moved");
                }

            }
        }).on("GET_PLAYERS", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                JSONArray objects = (JSONArray)args[0];
                try {
                    for(int i=0; i< objects.length(); i++) {
                        SpaceShip coopPlayer = new SpaceShip(friendlyShip, true);
                        Vector2 position = new Vector2();
                        position.x = ((Double)objects.getJSONObject(i).getDouble("x")).floatValue();
                        position.y = ((Double)objects.getJSONObject(i).getDouble("y")).floatValue();
                        coopPlayer.setPosition(position.x, position.y+300);

                        friendlyPlayer.put(objects.getJSONObject(i).getString("id"), coopPlayer);
                    }
                }
                catch (JSONException e) {

                }
            }
        });
    }
}
