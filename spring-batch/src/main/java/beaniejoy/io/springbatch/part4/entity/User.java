package beaniejoy.io.springbatch.part4.entity;

import java.time.LocalDate;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;

    @Enumerated(EnumType.STRING)
    private Level level = Level.NORMAL;

    private int totalAmount;

    private LocalDate updatedDate;

    @Builder
    private User(String username, int totalAmount) {
        this.username = username;
        this.totalAmount = totalAmount;
    }

    public boolean availableLevelUp() {
        return Level.availableLevelUp(this.getLevel(), this.getTotalAmount());
    }

    public Level levelUp() {
        Level nextLevel = Level.getNextLevel(this.getTotalAmount());

        this.level = nextLevel;
        this.updatedDate = LocalDate.now();

        return nextLevel;
    }
}
