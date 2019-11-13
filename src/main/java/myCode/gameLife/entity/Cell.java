package myCode.gameLife.entity;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@Table(name = "CELL")
public class Cell {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private int x;
    private int y;
    private boolean life;

    @ManyToOne(fetch = FetchType.EAGER, cascade = {CascadeType.ALL})
    @JoinColumn(name = "game_id")
    private Game game;
}
