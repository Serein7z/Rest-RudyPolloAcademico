
package pe.edu.utp.rudypollo.model;

import java.sql.Timestamp;


public class Pedido {
    private Integer id;
    private Integer clienteId;
    private Double total;
    private String estado;
    private Integer assignedRepartidorId;
    private Integer eta; // 
    private Timestamp creadoAt; 

 
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getClienteId() {
        return clienteId;
    }

    public void setClienteId(Integer clienteId) {
        this.clienteId = clienteId;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Integer getAssignedRepartidorId() {
        return assignedRepartidorId;
    }

    public void setAssignedRepartidorId(Integer assignedRepartidorId) {
        this.assignedRepartidorId = assignedRepartidorId;
    }

    public Integer getEta() {
        return eta;
    }

    public void setEta(Integer eta) {
        this.eta = eta;
    }

    public Timestamp getCreadoAt() {
        return creadoAt;
    }

    public void setCreadoAt(Timestamp creadoAt) {
        this.creadoAt = creadoAt;
    }

 
    @Override
    public String toString() {
        return "Pedido{" +
                "id=" + id +
                ", clienteId=" + clienteId +
                ", total=" + total +
                ", estado='" + estado + '\'' +
                ", assignedRepartidorId=" + assignedRepartidorId +
                ", eta=" + eta +
                ", creadoAt=" + creadoAt +
                '}';
    }
}