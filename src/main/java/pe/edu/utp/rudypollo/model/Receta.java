package pe.edu.utp.rudypollo.model;

public class Receta {
    private int id;
    private int idPlato;
    private int idIngrediente;
    private double cantidadNecesaria;

    public Receta() {}

    public Receta(int id, int idPlato, int idIngrediente, double cantidadNecesaria) {
        this.id = id;
        this.idPlato = idPlato;
        this.idIngrediente = idIngrediente;
        this.cantidadNecesaria = cantidadNecesaria;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdPlato() {
        return idPlato;
    }

    public void setIdPlato(int idPlato) {
        this.idPlato = idPlato;
    }

    public int getIdIngrediente() {
        return idIngrediente;
    }

    public void setIdIngrediente(int idIngrediente) {
        this.idIngrediente = idIngrediente;
    }

    public double getCantidadNecesaria() {
        return cantidadNecesaria;
    }

    public void setCantidadNecesaria(double cantidadNecesaria) {
        this.cantidadNecesaria = cantidadNecesaria;
    }

    @Override
    public String toString() {
        return "Receta{" +
                "id=" + id +
                ", idPlato=" + idPlato +
                ", idIngrediente=" + idIngrediente +
                ", cantidadNecesaria=" + cantidadNecesaria +
                '}';
    }
}
