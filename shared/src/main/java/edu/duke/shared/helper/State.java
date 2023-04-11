package edu.duke.shared.helper;

public enum State {
    // Waiting to join
    WAITING_TO_JOIN,
    // Waiting to initialize names
    READY_TO_INIT_NAME,
    // Waiting to initialize units
    READY_TO_INIT_UNITS,
    // Waiting for orders
    TURN_BEGIN,
    // Turn ends
    TURN_END,
    // Game ends
    GAME_OVER,
}
