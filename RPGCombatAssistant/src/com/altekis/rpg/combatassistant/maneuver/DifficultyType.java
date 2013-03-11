package com.altekis.rpg.combatassistant.maneuver;

import com.altekis.rpg.combatassistant.R;

public enum DifficultyType {

    ROUTINE(R.string.maneuver_diff_routine),
    EASY(R.string.maneuver_diff_easy),
    LIGHT(R.string.maneuver_diff_light),
    MEDIUM(R.string.maneuver_diff_medium),
    VERY_HARD(R.string.maneuver_diff_very_hard),
    EXTREMELY_HARD(R.string.maneuver_diff_extremely_hard),
    SHEER_HARD(R.string.maneuver_diff_sheer_hard),
    FOLLY(R.string.maneuver_diff_folly),
    ABSURD(R.string.maneuver_diff_absurd);

    private int title;

    private DifficultyType(int title) {
        this.title = title;
    }

    public int getTitle() {
        return title;
    }
}
