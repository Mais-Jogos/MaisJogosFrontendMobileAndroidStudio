package com.app.mais_jogos;

public class Review {

    private double notaReview;

    private String dataReview;

    private String descricaoReview;

    private String tituloReview;

    private Integer idJogo;

    private Integer idUser;

    public String getDataReview(){
        return dataReview;
    }
    public void setDataReview(String dataReview){
        this.dataReview = dataReview;
    }


    public double getNotaReview(){
        return notaReview;
    }
    public void setNotaReview(double notaReview){
        this.notaReview = notaReview;
    }

    public String getDescricaoReview(){
        return descricaoReview;
    }
    public void setDescricaoReview(String descricaoReview){
        this.descricaoReview = descricaoReview;
    }

    public Integer getIdJogo() {
        return idJogo;
    }

    public void setIdJogo(Integer idJogo) {
        this.idJogo = idJogo;
    }

    public String getTituloReview() {
        return tituloReview;
    }

    public void setTituloReview(String tituloReview) {
        this.tituloReview = tituloReview;
    }

    public Integer getIdUser() {
        return idUser;
    }

    public void setIdUser(Integer idUser) {
        this.idUser = idUser;
    }
}
