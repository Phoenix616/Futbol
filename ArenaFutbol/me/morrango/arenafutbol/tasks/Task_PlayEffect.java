package me.morrango.arenafutbol.tasks;

import me.morrango.arenafutbol.ArenaFutbol;

public class Task_PlayEffect implements Runnable {
    private ArenaFutbol plugin;

    public Task_PlayEffect(ArenaFutbol plugin) {
        this.plugin = plugin;
    }

    public void run() {
        this.plugin.doBallPhysics();
    }
}
