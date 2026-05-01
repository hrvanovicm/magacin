package api

//
//func (h *Handler) SignIn(c *gin.Context) {
//	var cmd account.SignInCommand
//	if err := c.ShouldBindJSON(&cmd); err != nil {
//		c.JSON(http.StatusBadRequest, gin.H{"error": err.Error()})
//		return
//	}
//
//	ctx, cancel := h.ctx()
//	defer cancel()
//
//	result, err := account.SignIn(account.NewApp(ctx, h.db), cmd)
//	if err != nil {
//		c.JSON(http.StatusUnauthorized, gin.H{"error": err.Error()})
//		return
//	}
//
//	c.JSON(http.StatusOK, result)
//}
//
//func (h *Handler) ListAccounts(c *gin.Context) {
//	ctx, cancel := h.ctx()
//	defer cancel()
//
//	qry := account.ListQuery{
//		Search:  optionalQuery(c, "search"),
//		OrderBy: optionalQuery(c, "order_by"),
//	}
//
//	accounts, err := account.List(account.NewApp(ctx, h.db), qry)
//	if err != nil {
//		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
//		return
//	}
//
//	c.JSON(http.StatusOK, accounts)
//}
//
//func (h *Handler) ListAccountsPaged(c *gin.Context) {
//	ctx, cancel := h.ctx()
//	defer cancel()
//
//	qry := account.ListPagedQuery{
//		ListQuery: account.ListQuery{
//			Search:  optionalQuery(c, "search"),
//			OrderBy: optionalQuery(c, "order_by"),
//		},
//		Paged: pagedFromQuery(c),
//	}
//
//	result, err := account.ListPaged(account.NewApp(ctx, h.db), qry)
//	if err != nil {
//		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
//		return
//	}
//
//	c.JSON(http.StatusOK, result)
//}
//
//func (h *Handler) GetAccount(c *gin.Context) {
//	id, ok := parseUintParam(c, "id")
//	if !ok {
//		return
//	}
//
//	ctx, cancel := h.ctx()
//	defer cancel()
//
//	acc, err := account.Get(account.NewApp(ctx, h.db), account.GetQuery{ID: id})
//	if err != nil {
//		c.JSON(http.StatusNotFound, gin.H{"error": err.Error()})
//		return
//	}
//
//	c.JSON(http.StatusOK, acc)
//}
//
//func (h *Handler) SaveAccount(c *gin.Context) {
//	var cmd account.SaveCommand
//	if err := c.ShouldBindJSON(&cmd); err != nil {
//		c.JSON(http.StatusBadRequest, gin.H{"error": err.Error()})
//		return
//	}
//
//	ctx, cancel := h.ctx()
//	defer cancel()
//
//	if err := account.Save(account.NewApp(ctx, h.db), cmd); err != nil {
//		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
//		return
//	}
//
//	c.JSON(http.StatusOK, gin.H{"message": "ok"})
//}
//
//func (h *Handler) DeleteAccount(c *gin.Context) {
//	id, ok := parseUintParam(c, "id")
//	if !ok {
//		return
//	}
//
//	ctx, cancel := h.ctx()
//	defer cancel()
//
//	if err := account.Delete(account.NewApp(ctx, h.db), account.DeleteCommand{ID: id}); err != nil {
//		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
//		return
//	}
//
//	c.JSON(http.StatusOK, gin.H{"message": "ok"})
//}
