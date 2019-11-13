package myCode.gameLife.service;

import myCode.gameLife.entity.Cell;
import myCode.gameLife.entity.Game;
import myCode.gameLife.repository.GameRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class GameService {

    private final GameRepository gameRepository;

    public GameService(GameRepository gameRepository) {
        this.gameRepository = gameRepository;
    }

    public Game getOldGame() {
        return null;
    }

    public Game newGeneration(Game game) {
        List<Cell> potentiallyLivingCells = new ArrayList<>();
        Cell nearCell = new Cell();
        game = gameRepository.save(game);
        List<Cell> newGenerationCell = new ArrayList<Cell>();
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
        for (Cell selectedCell : potentiallyLivingCells) {
            if (checkCell(selectedCell, game) && !newGenerationCell.contains(selectedCell)){
                selectedCell.setLife(true);
                newGenerationCell.add(selectedCell);
            }

        }

        for (Cell selectedCell : game.getCells()) {
            if (checkCell(selectedCell, game) && !newGenerationCell.contains(selectedCell)) newGenerationCell.add(selectedCell);
        }



        game.setCells(newGenerationCell);
        return game;
    }

    //Дополнительные методы, не использующие бд
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
        //правило, при котором живая на текущий момент клетка не умирает

        if (lifeCount == 3 && selectedCell.isLife() || lifeCount == 4 && selectedCell.isLife())//3 или 4 потому как своя клетка тоже считается живой(сделал для корректности результата)
            return true;
        //правило, при котором рождается новая клетка
        return lifeCount == 3 && !selectedCell.isLife();
        //во всех других случаях ничего не происходит
    }

    //проверка на выход за поля;
    private boolean checkBorder(Cell selectedCell, Game game) {
        selectedCell.setX(selectedCell.getX() < 1 ? game.getPlayingFieldSize() : selectedCell.getX());
        selectedCell.setX(selectedCell.getX() > game.getPlayingFieldSize() ? 1 : selectedCell.getX());
        selectedCell.setY(selectedCell.getY() < 1 ? game.getPlayingFieldSize() : selectedCell.getY());
        selectedCell.setY(selectedCell.getY() > game.getPlayingFieldSize() ? 1 : selectedCell.getY());
        return true;
    }
}

