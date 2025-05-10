package co.edu.uniquindio.ingesis.model;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import io.quarkus.hibernate.orm.panache.PanacheEntity;

import co.edu.uniquindio.ingesis.domain.Rol;
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User extends PanacheEntityBase {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        public Long id;
        private String usuario;
        private String email;
        private String clave;
        @Enumerated(EnumType.STRING)
        private Rol rol;

        public Long getId() {
                return id;
        }

        public void setId(Long id) {
                this.id = id;
        }

        public String getUsuario() {
                return usuario;
        }

        public void setUsuario(String usuario) {
                this.usuario = usuario;
        }

        public String getEmail() {
                return email;
        }

        public void setEmail(String email) {
                this.email = email;
        }

        public String getClave() {
                return clave;
        }

        public void setClave(String clave) {
                this.clave = clave;
        }

        public Rol getRol() {
                return rol;
        }

        public void setRol(Rol rol) {
                this.rol = rol;
        }
}