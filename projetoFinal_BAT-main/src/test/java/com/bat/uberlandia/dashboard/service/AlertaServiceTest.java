package com.bat.uberlandia.dashboard.service;

import com.bat.uberlandia.dashboard.model.*;
import com.bat.uberlandia.dashboard.repository.ChamadoRepository;
import com.bat.uberlandia.dashboard.repository.NotificacaoRepository;
import com.bat.uberlandia.dashboard.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AlertaServiceTest {

    @Mock
    private ChamadoRepository chamadoRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private NotificacaoRepository notificacaoRepository;

    @InjectMocks
    private AlertaService alertaService;

    @Test
    void contarAlertasNaoLidos_ShouldReturnCount() {
        when(notificacaoRepository.findByLidaFalseOrderByDataEnvioDesc()).thenReturn(List.of(new Notificacao(), new Notificacao()));
        long count = alertaService.contarAlertasNaoLidos();
        assertEquals(2, count);
    }
}
