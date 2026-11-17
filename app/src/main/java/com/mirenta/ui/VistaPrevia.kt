package com.mirenta.ui

import com.mirenta.data.EnMemoriaDeclaracionRepository
import com.mirenta.data.EnMemoriaPerfilRepository

fun rentaViewModelDePrueba(): RentaViewModel = RentaViewModel(EnMemoriaDeclaracionRepository()) { null }

fun perfilViewModelDePrueba(): PerfilViewModel = PerfilViewModel(EnMemoriaPerfilRepository())
