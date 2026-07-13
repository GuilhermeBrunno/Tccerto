package com.bat.uberlandia.dashboard.service;

import com.bat.uberlandia.dashboard.model.Chamado;
import com.bat.uberlandia.dashboard.model.Maquina;
import com.bat.uberlandia.dashboard.model.Chamado.Status;
import com.bat.uberlandia.dashboard.repository.ChamadoRepository;
import com.bat.uberlandia.dashboard.repository.NotificacaoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChamadoServiceTest {

    @Mock
    private ChamadoRepository chamadoRepository;

    @Mock
    private NotificacaoRepository notificacaoRepository;

    @InjectMocks
    private ChamadoService chamadoService;

    private Chamado chamado;
    private Maquina maquina;

    @BeforeEach
    void setUp() {
        maquina = new Maquina();
        maquina.setId(1L);
        maquina.setNome("Maquina 1");

        chamado = new Chamado();
        chamado.setId(1L);
        chamado.setTitulo("Problema");
    }

    @Test
    void abrirChamado_ShouldSetStatusAndMaquina() {
        when(chamadoRepository.save(any(Chamado.class))).thenReturn(chamado);

        Chamado result = chamadoService.abrirChamado(chamado, maquina);

        assertEquals(Status.ABERTO, result.getStatus());
        assertEquals(maquina, result.getMaquina());
        assertNotNull(result.getDataAbertura());
        verify(chamadoRepository, times(1)).save(chamado);
    }

    @Test
    void iniciarAtendimento_ShouldUpdateStatusAndFields() {
        when(chamadoRepository.findById(1L)).thenReturn(Optional.of(chamado));
        when(chamadoRepository.save(any(Chamado.class))).thenReturn(chamado);

        chamadoService.iniciarAtendimento(1L, null);

        assertEquals(Status.EM_ANDAMENTO, chamado.getStatus());
        assertNotNull(chamado.getInicioReparo());
        verify(chamadoRepository, times(1)).save(chamado);
    }

    @Test
    void iniciarAtendimento_ShouldThrowExceptionIfNotFound() {
        when(chamadoRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> chamadoService.iniciarAtendimento(1L, null));
    }
}
