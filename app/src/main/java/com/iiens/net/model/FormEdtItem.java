package com.iiens.net.model;

/**
 * FormEdtItem
 * Modèle d'un choix du formulaire de l'emploi du temps
 */

public class FormEdtItem {
    private long id;
    private String code;
    private String label;
    private int annee;
    private int option_group;
    private int option_num;
    private boolean isComm;
    private boolean isLangue;

    public long getId() { return id; }

    public void setId(long id) {
        this.id = id;
    }

    public String getCode() { return this.code; }

    public void setCode(String code) { this.code = code; }

    public String getLabel() { return this.label; }

    public void setLabel(String label) { this.label = label; }

    public int getAnnee() { return this.annee; }

    public void setAnnee(int annee) { this.annee = annee; }

    public int getOption_group() { return this.option_group; }

    public void setOption_group(int option_group) { this.option_group = option_group; }

    public int getOption_num() { return this.option_num; }

    public void setOption_num(int option_num) { this.option_num = option_num; }

    public boolean isComm() { return this.isComm; }

    public void setIsComm(boolean isComm) { this.isComm = isComm; }

    public boolean isLangue() { return this.isLangue; }

    public void setIsLangue(boolean isLangue) { this.isLangue = isLangue; }
}