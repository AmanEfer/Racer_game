package com.javarush.games.racer.road;

import com.javarush.engine.cell.Game;
import com.javarush.games.racer.PlayerCar;
import com.javarush.games.racer.RacerGame;
import com.javarush.games.racer.RoadMarking;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class RoadManager {
    public static final int LEFT_BORDER = RacerGame.ROADSIDE_WIDTH;
    public static final int RIGHT_BORDER = RacerGame.WIDTH - LEFT_BORDER;
    private static final int FIRST_LANE_POSITION = 16;
    private static final int FOURTH_LANE_POSITION = 44;
    private static final int PLAYER_CAR_DISTANCE = 12;
    private List<RoadObject> items = new ArrayList<>();
    private int passedCarsCount;

    public int getPassedCarsCount() {
        return passedCarsCount;
    }

    private RoadObject createRoadObject(RoadObjectType type, int x, int y) {
        if (type == RoadObjectType.THORN)
            return new Thorn(x, y);
        else if (type == RoadObjectType.DRUNK_CAR)
            return new MovingCar(x, y);
        else
            return new Car(type, x, y);
    }

    private void addRoadObject(RoadObjectType type, Game game) {
        int x = game.getRandomNumber(FIRST_LANE_POSITION, FOURTH_LANE_POSITION);
        int y = -1 * RoadObject.getHeight(type);
        RoadObject roadObject = createRoadObject(type, x, y);

        if (isRoadSpaceFree(roadObject))
            items.add(roadObject);
    }

    public void draw(Game game) {
        items.forEach(item -> item.draw(game));
    }

    public void move(int boost) {
        items.forEach(item -> item.move(boost + item.speed, items));
        deletePassedItems();
    }

    private boolean isThornExists() {
        return items.stream()
                .anyMatch(item -> item.type == RoadObjectType.THORN);
    }

    private boolean isMovingCarExists() {
        return items.stream()
                .anyMatch(item -> item.type == RoadObjectType.DRUNK_CAR);
    }

    private void generateThorn(Game game) {
        int randomNumber = game.getRandomNumber(100);
        if (randomNumber < 10 && !isThornExists())
            addRoadObject(RoadObjectType.THORN, game);
    }

    private void generateRegularCar(Game game) {
        int randomNumber = game.getRandomNumber(100);
        int carTypeNumber = game.getRandomNumber(4);
        if (randomNumber < 30)
            addRoadObject(RoadObjectType.values()[carTypeNumber], game);
    }

    private void generateMovingCar(Game game) {
        int randomNumber = game.getRandomNumber(100);
        if (randomNumber < 10 && !isMovingCarExists())
            addRoadObject(RoadObjectType.DRUNK_CAR, game);
    }

    public void generateNewRoadObjects(Game game) {
        generateThorn(game);
        generateRegularCar(game);
        generateMovingCar(game);
    }

    private void deletePassedItems() {
        Iterator<RoadObject> it = items.iterator();
        while (it.hasNext()) {
            RoadObject item = it.next();
            if (item.y >= RacerGame.HEIGHT) {
                it.remove();
                if (item.type != RoadObjectType.THORN)
                    passedCarsCount++;
            }
        }
    }

    public boolean checkCrush(PlayerCar car) {
        for (RoadObject item : items) {
            boolean isCrush = item.isCollision(car);
            if (isCrush) {
                return true;
            }
        }
        return false;
    }

    private boolean isRoadSpaceFree(RoadObject object) {
        for (RoadObject item : items) {
            boolean isLowDistance = item.isCollisionWithDistance(object, PLAYER_CAR_DISTANCE);
            if (isLowDistance)
                return false;
        }
        return true;
    }
}
