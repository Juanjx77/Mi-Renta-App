-- Contexto Identidad y Perfil
CREATE TABLE perfil (
    usuario_id        VARCHAR(128) PRIMARY KEY,
    nombre            VARCHAR(200) NOT NULL,
    tipo_documento    VARCHAR(60)  NOT NULL,
    numero_documento  VARCHAR(30)  NOT NULL,
    telefono          VARCHAR(30)  NOT NULL,
    fecha_nacimiento  VARCHAR(20)  NOT NULL,
    tiene_rut         VARCHAR(5)   NOT NULL,
    responsable_iva   VARCHAR(5)   NOT NULL,
    actualizado_en    TIMESTAMP WITH TIME ZONE NOT NULL
);

-- Contexto Declaración
CREATE TABLE declaracion (
    id                  UUID PRIMARY KEY,
    usuario_id          VARCHAR(128) NOT NULL,
    anio_gravable       SMALLINT     NOT NULL,
    estado              VARCHAR(20)  NOT NULL,
    version             BIGINT       NOT NULL,
    salario             BIGINT NOT NULL,
    honorarios          BIGINT NOT NULL,
    arriendos           BIGINT NOT NULL,
    intereses           BIGINT NOT NULL,
    aportes             BIGINT NOT NULL,
    vivienda            BIGINT NOT NULL,
    dependientes        BIGINT NOT NULL,
    prepagada           BIGINT NOT NULL,
    afc                 BIGINT NOT NULL,
    retenciones         BIGINT NOT NULL,
    patrimonio          BIGINT NOT NULL,
    deudas              BIGINT NOT NULL,
    consumos            BIGINT NOT NULL,
    secciones_guardadas VARCHAR(200) NOT NULL,
    impuesto_calculado  BIGINT NOT NULL,
    actualizado_en      TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT uq_declaracion_usuario_anio UNIQUE (usuario_id, anio_gravable)
);
