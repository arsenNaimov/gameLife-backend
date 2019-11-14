package myCode.gameLife.entity;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@Table(name = "CELLS")
public class Cell {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private int x;
    private int y;
    private boolean life;
}
