package myCode.gameLife.service;

import myCode.gameLife.entity.Cell;
import myCode.gameLife.entity.Game;
import myCode.gameLife.repository.GameRepository;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class GameService {

    private long oldGameID = 0; //id предыдущей игры
    private final GameRepository gameRepository;
    public GameService(GameRepository gameRepository) {
        this.gameRepository = gameRepository;
    }


    public Game getOldGame() {
        return gameRepository.findById(oldGameID)
                .orElse(null);
    }


    public Game newGeneration(Game game) {
        List<Cell> potentiallyLivingCells = new ArrayList<>();

        List<Cell> newGenerationCell = new ArrayList<>();
        for (Cell selectedCell : game.getCells()) {

            for (int x = -1; x < 2; x++) {
                for (int y = -1; y < 2; y++) {
                    Cell cell = new Cell();
                    cell.setX(selectedCell.getX() + x);
                    cell.setY(selectedCell.getY() + y);
                    if (checkBorder(cell, game)
                            && cell.getX() != selectedCell.getX()
                            && cell.getY() != selectedCell.getY()) potentiallyLivingCells.add(cell);
                }
            }
        }
        for (Cell potentialCell : potentiallyLivingCells) {
            if (checkCell(potentialCell, game)){
                potentialCell.setLife(true);
                newGenerationCell.add(potentialCell);
            }

        }
        for (Cell selectedCell : game.getCells()) {
            if (checkCell(selectedCell, game) ) newGenerationCell.add(selectedCell);
        }
        Set<Cell> set = new HashSet<>(newGenerationCell);
        game.setCells(set);
        game = gameRepository.save(game);
        this.oldGameID = game.getId();
        return game;
    }
    //Дополнительные методы, не использующие DB
    private boolean checkCell(Cell selectedCell, Game game) {
        Cell nearCell = new Cell();
        int lifeCount = 0; //счетчик для вычисления жива ли клетка;
        for (int x = -1; x < 2; x++) {
            for (int y = -1; y < 2; y++) {
                nearCell.setX(selectedCell.getX() + x);
                nearCell.setY(selectedCell.getY() + y);
                checkBorder(nearCell, game);
                for (Cell lifeCell : game.getCells()) {
                    if (nearCell.getX() == lifeCell.getX() && nearCell.getY() == lifeCell.getY()) lifeCount++;
                }
            }
        }

        //проверка соответствия живой клетки к условиям выживания
        if (lifeCount == 3 && selectedCell.isLife() || lifeCount == 4 && selectedCell.isLife())return true;//3 или 4 потому как своя клетка тоже считается живой(сделал для корректности результата)
        //проверка условий для рождения клетки
        return lifeCount == 3 && !selectedCell.isLife();
    }
    //проверка на выход за поля
    private boolean checkBorder(Cell selectedCell, Game game) {
        selectedCell.setX(selectedCell.getX() < 1 ? game.getPlayingFieldSize() : selectedCell.getX());
        selectedCell.setX(selectedCell.getX() > game.getPlayingFieldSize() ? 1 : selectedCell.getX());
        selectedCell.setY(selectedCell.getY() < 1 ? game.getPlayingFieldSize() : selectedCell.getY());
        selectedCell.setY(selectedCell.getY() > game.getPlayingFieldSize() ? 1 : selectedCell.getY());
        return true;
    }
}

