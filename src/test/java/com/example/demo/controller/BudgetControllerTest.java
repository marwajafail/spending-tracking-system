package com.example.demo.controller;

import com.example.demo.dao.GenericDao;
import com.example.demo.dto.BudgetDto;
import com.example.demo.service.BudgetService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class BudgetControllerTest {

    @Mock
    private BudgetService budgetService;


    @InjectMocks
    private BudgetController budgetController;

    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(budgetController).build();
    }

    @Test
    public void testGetAllBudgets() throws Exception {
        List<BudgetDto> budgets = new ArrayList<>();
        when(budgetService.getAll(false)).thenReturn(budgets);

        mockMvc.perform(get("/api/budget")
                .param("details", "false"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errors[0]").value("No budgets found"));

        verify(budgetService, times(1)).getAll(false);
    }

    @Test
    public void testCreateBudget() throws Exception {
        BudgetDto budgetDto = new BudgetDto();
        when(budgetService.createBudget(any(BudgetDto.class)))
                .thenReturn(new GenericDao<>(budgetDto, new ArrayList<>()));

        mockMvc.perform(post("/api/budget")
                .contentType("application/json")
                .content("{\"key\":\"value\"}"))
                .andExpect(status().isCreated());

        verify(budgetService, times(1)).createBudget(any(BudgetDto.class));
    }

    @Test
    public void testGetBudgetById() throws Exception {
        BudgetDto budgetDto = new BudgetDto();
        when(budgetService.getById(1L, false)).thenReturn(budgetDto);

        mockMvc.perform(get("/api/budget/1")
                .param("details", "false"))
                .andExpect(status().isOk());

        verify(budgetService, times(1)).getById(1L, false);
    }

    @Test
    public void testEditBudget() throws Exception {
        BudgetDto budgetDto = new BudgetDto();
        when(budgetService.editBudget(any(BudgetDto.class)))
                .thenReturn(new GenericDao<>(budgetDto, new ArrayList<>()));

        mockMvc.perform(put("/api/budget")
                .contentType("application/json")
                .content("{\"key\":\"value\"}"))
                .andExpect(status().isCreated());

        verify(budgetService, times(1)).editBudget(any(BudgetDto.class));
    }

    @Test
    public void testDeleteBudget() throws Exception {
        when(budgetService.deleteBudget(1L)).thenReturn(new GenericDao<>(true, new ArrayList<>()));

        mockMvc.perform(delete("/api/budget/1"))
                .andExpect(status().isCreated());

        verify(budgetService, times(1)).deleteBudget(1L);
    }



}

