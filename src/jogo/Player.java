package jogo;

import java.util.Random;
import java.util.concurrent.TimeUnit;

public class Player {
    private int pontos_vida, pontos_dano;
    private Random gerador = new Random();
    private Room sala_atual;
    private boolean in_combat;
    private boolean possuiBolaFogo;

    public Player() {
        pontos_dano = 1;
        pontos_vida = 10;
        in_combat = false;
        possuiBolaFogo = false;
    }

    // overloading do construtor: Opcionalmente criamos com uma sala inicial
    public Player(Room sala_inicial) {
        pontos_dano = 1;
        pontos_vida = 10;
        sala_atual = sala_inicial;
        in_combat = false;
        possuiBolaFogo = false;
    }

    public Room getSala_atual() {
        return sala_atual;
    }

    public void printaInfoVitoria() {
        System.out.println("Uma luz azul brilha no corpo morto do chefe dos monstros...");
        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            System.err.format("IOException: %s%n", e);
        }
        System.out.println("Você encontra a safira que tanto procurava, parabéns!");
        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            System.err.format("IOException: %s%n", e);
        }
        System.out.println("                         `-+sdmhmMMhyhNMMddm`");
        System.out.println("                      .smdy+:`  `-smmNy/    :h\\");
        System.out.println("                    -yNs.          mmhmo.    Md");
        System.out.println("                  -hNo`           oM- .oNh:  :s");
        System.out.println("                :dm+`            .Ns    `/dmo.d");
        System.out.println("               +Mo`              hN        yMMM");
        System.out.println("             `sN/              /dMmsssoo++///NM");
        System.out.println("            `hN-             +md/sM.        hNM");
        System.out.println("           .dm.            +mh:  .Ns      .dm+M");
        System.out.println("           +My          .oNy-     sM.    :Nd.+M");
        System.out.println("           +MM+     .:/sNd-       .Ns   sNo  sM");
        System.out.println("          `+MyMyhdddysmMoydmhs+:smdsM.+Nh-   hN");
        System.out.println("          `+M.MM:`   -Mo           hMNm/     md");
        System.out.println("          `+M  hm.   dm`         `+Nmdm+     My");
        System.out.println("          `/M:  md` /M/        -sNy:  -yNs. .Mo");
        System.out.println("           `Nh   Nh`Nh      :smd+.      .sNyoM/");
        System.out.println("            -mh:mh:MmNss:+sdNds:-::///++oosyN-");
    }

    public boolean checaJogoFinalizado() {
        if (sala_atual.getAtributo() == Attribute.CHEFE && !sala_atual.atributoEstaAtivo()) {
            printaInfoVitoria(); // Assim que o boss (chefe) morrer o jogo é finalizado.
            return true;
        } else if (pontos_vida == 0) {
            System.out.println("Você morreu  \u2620 \u2620! Boa sorte na próxima vez.");
            return true; // A outra forma de finalizar o jogo é com o player morrendo.
        }
        return false;
    }

    public void printStats() {
        System.out.println("Seus pontos de vida: " + pontos_vida + "\u2764\uFE0F");
    }

    public void printStats(Monster monstro) {
        System.out.println("Seus pontos de vida: " + pontos_vida + "\u2764\uFE0F" +
                "\t\tPontos de vida do monstro: " + monstro.getPontos_vida() + " \uD83D\uDC7A" + "\n");
    }

    public void entra_combate(boolean usoFeitico) {
        if (sala_atual.temMonstro()) {
            Monster monstro = sala_atual.getMonstro();
            if (!monstro.estaVivo())
                System.out.println(sala_atual.getAttributeDescription());
            else {
                System.out.println(ataque(monstro, usoFeitico));
                printStats(monstro);
                // Se o monstro estiver vivo, o monstro da sala tenta atacar
                sala_atual.ataqueMonstro(this);
            }
        } else
            System.out.println("Não há monstros nessa sala.");
    }

    private String ataque(Monster monstro, boolean usoFeitico) {
        in_combat = true;
        String resultado = new String();
        System.out.println("Você se prepara para atacar...");
        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            System.err.format("IOException: %s%n", e);
        }
        if (usoFeitico) {
            if (!possuiBolaFogo) {
                resultado = "Você não possui o feitiço de bola de fogo!";
                // possível acerto do feitico
            } else if (gerador.nextBoolean()) {
                int dano_bola_fogo = Attribute.BOLAFOGO.getValor_associado();
                monstro.sofre_dano(dano_bola_fogo);
                resultado = "Seu feitiço de bola de fogo acertou o monstro! Ele deu " + dano_bola_fogo + " de dano";
            } else {
                resultado = "Seu feitiço não acertou, mas você pode tentar usá-lo novamente";
            }
        }
        // possível acerto
        else if (gerador.nextBoolean()) {
            // possível crítico
            if (gerador.nextBoolean()) {
                monstro.sofre_dano(pontos_dano * 2);
                resultado = "Seu ataque foi crítico! Ele deu o dobro de dano (" + pontos_dano * 2 + ")";
            } else {
                monstro.sofre_dano(pontos_dano);
                resultado = "Seu ataque acertou o monstro!";
            }
            if (!monstro.estaVivo()) {
                resultado += "\nO seu ataque matou o monstro. \u2620";
                in_combat = false;
                // Caso se tratar duma sala com o feitiço de bola de fogo
                if (sala_atual.getAtributo().equals(Attribute.BOLAFOGO)) {
                    possuiBolaFogo = true;
                    resultado += "\nVocê ganhou o feitiço Bola de Fogo, para utlizá-lo use o comando 'feitico' quando enfrentar o próximo monstro.";
                }
            }
        } else
            resultado = "Que pena, seu ataque não acertou o monstro!";
        return resultado;
    }

    // Método que subtrai o dano sofrido dos pontos de vida do monstro
    public void sofre_dano(int dano_sofrido) {
        pontos_vida -= dano_sofrido;
    }

    public void recebe_cura(int quant_cura) {
        pontos_vida += quant_cura;
        if (pontos_vida > 10)
            // limitamos a quantidade máxima de HP a 10
            pontos_vida = 10;
    }

    /**
     * Try to go in one direction. If there is an exit, enter the new
     * room, otherwise print an error message.
     */
    public void goRoom(Command command) {
        if (!command.hasSecondWord()) {
            // if there is no second word, we don't know where to go...
            System.out.println("Ir aonde?");
            return;
        }

        String direction = command.getSecondWord();

        // Try to leave current room.
        Room nextRoom = sala_atual.getExit(direction);

        if (nextRoom == null) {
            System.out.println("Não há porta nessa direção!");
        } else if (in_combat) {
            System.out.println("Nada de fugir. É preciso primeiro derrotar o monstro!");
        } else {
            sala_atual = nextRoom;
            System.out.println(sala_atual.getLongDescription());
            sala_atual.usaAtributo(this);
        }
    }

    /*
     * Método que retorna se o jogador está em combate
     * Um jogador está em combate quando existe um monstro vivo na mesma sala que
     * ele
     */
    public boolean isInCombat() {
        return in_combat;
    }

    public void setIn_combat(boolean in_combat) {
        this.in_combat = in_combat;
    }

    public int getPontos_dano() {
        return pontos_dano;
    }

    public int getPontos_vida() {
        return pontos_vida;
    }

    public boolean getPossuiBolaFogo() {
        return possuiBolaFogo;
    }

    public void setBola_fogo(boolean bola_fogo) {
        this.possuiBolaFogo = bola_fogo;
    }
}
