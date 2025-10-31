package pe.edu.utp.rudypollo.dao;

import java.util.List;
import pe.edu.utp.rudypollo.model.Receta;

public interface RecetaDAO {
    List<Receta> findByIngredienteId(int idIngrediente);
    List<Receta> findByPlatoId(int idPlato);
}
