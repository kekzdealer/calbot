package at.kurumi.routines;

import javax.annotation.PostConstruct;

@jakarta.ejb.Startup
public class Startup {

    @PostConstruct
    public void startup() {

    }

    private void startDatabase() {
        // start create/start main db container

        // wait for db to be up

        // -> start discord interface
    }

    private void startDiscordInterface() {

    }

}
