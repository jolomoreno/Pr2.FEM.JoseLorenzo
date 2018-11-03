package es.upm.miw.firebaselogin.models;

public class Reparto {
    private String fechaEntrega;
    private String repartidor;
    private String producto;
    private String incidencia;
    private String uriImagen;

    public Reparto() {
    }

    @Override
    public String toString() {
        return "Reparto{" +
                "fechaEntrega='" + fechaEntrega + '\'' +
                ", repartidor='" + repartidor + '\'' +
                ", producto='" + producto + '\'' +
                ", incidencia='" + incidencia + '\'' +
                ", uriImagen='" + uriImagen + '\'' +
                '}';
    }

    public String getFechaEntrega() {
        return fechaEntrega;
    }

    public void setFechaEntrega(String fechaEntrega) {
        this.fechaEntrega = fechaEntrega;
    }

    public String getRepartidor() {
        return repartidor;
    }

    public void setRepartidor(String repartidor) {
        this.repartidor = repartidor;
    }

    public String getProducto() {
        return producto;
    }

    public void setProducto(String producto) {
        this.producto = producto;
    }

    public String getIncidencia() {
        return incidencia;
    }

    public void setIncidencia(String incidencia) {
        this.incidencia = incidencia;
    }

    public String getUriImagen() {
        return uriImagen;
    }

    public void setUriImagen(String uriImagen) {
        this.uriImagen = uriImagen;
    }

    /*
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.fechaEntrega);
        dest.writeString(this.repartidor);
        dest.writeString(this.producto);
        dest.writeString(this.incidencia);
    }

    protected Reparto(Parcel in) {
        this.fechaEntrega = in.readString();
        this.repartidor = in.readString();
        this.producto = in.readString();
        this.incidencia = in.readString();
    }

    public static final Parcelable.Creator<Reparto> CREATOR = new Parcelable.Creator<Reparto>() {
        @Override
        public Reparto createFromParcel(Parcel source) {
            return new Reparto(source);
        }

        @Override
        public Reparto[] newArray(int size) {
            return new Reparto[size];
        }
    };*/
}
