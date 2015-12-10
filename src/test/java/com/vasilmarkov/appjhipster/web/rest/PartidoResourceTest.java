package com.vasilmarkov.appjhipster.web.rest;

import com.vasilmarkov.appjhipster.Application;
import com.vasilmarkov.appjhipster.domain.Partido;
import com.vasilmarkov.appjhipster.repository.PartidoRepository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.hamcrest.Matchers.hasItem;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


/**
 * Test class for the PartidoResource REST controller.
 *
 * @see PartidoResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest
public class PartidoResourceTest {

    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");


    private static final Integer DEFAULT_LOCAL_PUNTOS = 0;
    private static final Integer UPDATED_LOCAL_PUNTOS = 1;

    private static final Integer DEFAULT_PUNTOS_VISITANTE = 0;
    private static final Integer UPDATED_PUNTOS_VISITANTE = 1;

    private static final DateTime DEFAULT_INICIO_PARTIDO = new DateTime(0L, DateTimeZone.UTC);
    private static final DateTime UPDATED_INICIO_PARTIDO = new DateTime(DateTimeZone.UTC).withMillisOfSecond(0);
    private static final String DEFAULT_INICIO_PARTIDO_STR = dateTimeFormatter.print(DEFAULT_INICIO_PARTIDO);

    private static final DateTime DEFAULT_FINAL_PARTIDO = new DateTime(0L, DateTimeZone.UTC);
    private static final DateTime UPDATED_FINAL_PARTIDO = new DateTime(DateTimeZone.UTC).withMillisOfSecond(0);
    private static final String DEFAULT_FINAL_PARTIDO_STR = dateTimeFormatter.print(DEFAULT_FINAL_PARTIDO);

    @Inject
    private PartidoRepository partidoRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    private MockMvc restPartidoMockMvc;

    private Partido partido;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        PartidoResource partidoResource = new PartidoResource();
        ReflectionTestUtils.setField(partidoResource, "partidoRepository", partidoRepository);
        this.restPartidoMockMvc = MockMvcBuilders.standaloneSetup(partidoResource).setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        partido = new Partido();
        partido.setLocalPuntos(DEFAULT_LOCAL_PUNTOS);
        partido.setPuntosVisitante(DEFAULT_PUNTOS_VISITANTE);
        partido.setInicioPartido(DEFAULT_INICIO_PARTIDO);
        partido.setFinalPartido(DEFAULT_FINAL_PARTIDO);
    }

    @Test
    @Transactional
    public void createPartido() throws Exception {
        int databaseSizeBeforeCreate = partidoRepository.findAll().size();

        // Create the Partido

        restPartidoMockMvc.perform(post("/api/partidos")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(partido)))
                .andExpect(status().isCreated());

        // Validate the Partido in the database
        List<Partido> partidos = partidoRepository.findAll();
        assertThat(partidos).hasSize(databaseSizeBeforeCreate + 1);
        Partido testPartido = partidos.get(partidos.size() - 1);
        assertThat(testPartido.getLocalPuntos()).isEqualTo(DEFAULT_LOCAL_PUNTOS);
        assertThat(testPartido.getPuntosVisitante()).isEqualTo(DEFAULT_PUNTOS_VISITANTE);
        assertThat(testPartido.getInicioPartido().toDateTime(DateTimeZone.UTC)).isEqualTo(DEFAULT_INICIO_PARTIDO);
        assertThat(testPartido.getFinalPartido().toDateTime(DateTimeZone.UTC)).isEqualTo(DEFAULT_FINAL_PARTIDO);
    }

    @Test
    @Transactional
    public void checkInicioPartidoIsRequired() throws Exception {
        int databaseSizeBeforeTest = partidoRepository.findAll().size();
        // set the field null
        partido.setInicioPartido(null);

        // Create the Partido, which fails.

        restPartidoMockMvc.perform(post("/api/partidos")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(partido)))
                .andExpect(status().isBadRequest());

        List<Partido> partidos = partidoRepository.findAll();
        assertThat(partidos).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkFinalPartidoIsRequired() throws Exception {
        int databaseSizeBeforeTest = partidoRepository.findAll().size();
        // set the field null
        partido.setFinalPartido(null);

        // Create the Partido, which fails.

        restPartidoMockMvc.perform(post("/api/partidos")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(partido)))
                .andExpect(status().isBadRequest());

        List<Partido> partidos = partidoRepository.findAll();
        assertThat(partidos).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllPartidos() throws Exception {
        // Initialize the database
        partidoRepository.saveAndFlush(partido);

        // Get all the partidos
        restPartidoMockMvc.perform(get("/api/partidos"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(partido.getId().intValue())))
                .andExpect(jsonPath("$.[*].localPuntos").value(hasItem(DEFAULT_LOCAL_PUNTOS)))
                .andExpect(jsonPath("$.[*].puntosVisitante").value(hasItem(DEFAULT_PUNTOS_VISITANTE)))
                .andExpect(jsonPath("$.[*].inicioPartido").value(hasItem(DEFAULT_INICIO_PARTIDO_STR)))
                .andExpect(jsonPath("$.[*].finalPartido").value(hasItem(DEFAULT_FINAL_PARTIDO_STR)));
    }

    @Test
    @Transactional
    public void getPartido() throws Exception {
        // Initialize the database
        partidoRepository.saveAndFlush(partido);

        // Get the partido
        restPartidoMockMvc.perform(get("/api/partidos/{id}", partido.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(partido.getId().intValue()))
            .andExpect(jsonPath("$.localPuntos").value(DEFAULT_LOCAL_PUNTOS))
            .andExpect(jsonPath("$.puntosVisitante").value(DEFAULT_PUNTOS_VISITANTE))
            .andExpect(jsonPath("$.inicioPartido").value(DEFAULT_INICIO_PARTIDO_STR))
            .andExpect(jsonPath("$.finalPartido").value(DEFAULT_FINAL_PARTIDO_STR));
    }

    @Test
    @Transactional
    public void getNonExistingPartido() throws Exception {
        // Get the partido
        restPartidoMockMvc.perform(get("/api/partidos/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updatePartido() throws Exception {
        // Initialize the database
        partidoRepository.saveAndFlush(partido);

		int databaseSizeBeforeUpdate = partidoRepository.findAll().size();

        // Update the partido
        partido.setLocalPuntos(UPDATED_LOCAL_PUNTOS);
        partido.setPuntosVisitante(UPDATED_PUNTOS_VISITANTE);
        partido.setInicioPartido(UPDATED_INICIO_PARTIDO);
        partido.setFinalPartido(UPDATED_FINAL_PARTIDO);
        

        restPartidoMockMvc.perform(put("/api/partidos")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(partido)))
                .andExpect(status().isOk());

        // Validate the Partido in the database
        List<Partido> partidos = partidoRepository.findAll();
        assertThat(partidos).hasSize(databaseSizeBeforeUpdate);
        Partido testPartido = partidos.get(partidos.size() - 1);
        assertThat(testPartido.getLocalPuntos()).isEqualTo(UPDATED_LOCAL_PUNTOS);
        assertThat(testPartido.getPuntosVisitante()).isEqualTo(UPDATED_PUNTOS_VISITANTE);
        assertThat(testPartido.getInicioPartido().toDateTime(DateTimeZone.UTC)).isEqualTo(UPDATED_INICIO_PARTIDO);
        assertThat(testPartido.getFinalPartido().toDateTime(DateTimeZone.UTC)).isEqualTo(UPDATED_FINAL_PARTIDO);
    }

    @Test
    @Transactional
    public void deletePartido() throws Exception {
        // Initialize the database
        partidoRepository.saveAndFlush(partido);

		int databaseSizeBeforeDelete = partidoRepository.findAll().size();

        // Get the partido
        restPartidoMockMvc.perform(delete("/api/partidos/{id}", partido.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<Partido> partidos = partidoRepository.findAll();
        assertThat(partidos).hasSize(databaseSizeBeforeDelete - 1);
    }
}
