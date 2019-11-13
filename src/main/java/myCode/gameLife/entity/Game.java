package myCode.gameLife.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Entity
@Data
@Table(name = "GAMES")
public class Game {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private int playingFieldSize;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "game")
    private List<Cell> cells;
}
