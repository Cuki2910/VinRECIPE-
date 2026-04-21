package com.vinrecipe.controller;

import com.vinrecipe.model.User;
import com.vinrecipe.service.RecipeService;
import com.vinrecipe.service.SearchService;

/**
 * Interface for controllers that need access to the shared application context.
 * Allows MainController to inject dependencies when loading child views.
 */
public interface ContextAware {
    void setContext(User currentUser, RecipeService recipeService,
                    SearchService searchService, MainController mainController);
}
