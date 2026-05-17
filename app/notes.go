package app

import "hrvanovicm/magacin/internal/notes"

type GetNoteRequest = notes.GetQuery
type SaveNoteRequest = notes.SaveCommand

func (a *WailsApp) GetNote(req GetNoteRequest) (*notes.Note, error) {
	res, err := notes.Get(a.getRequest(), req)
	if err != nil {
		a.report(err)
	}
	return res, err
}

func (a *WailsApp) SaveNote(req SaveNoteRequest) error {
	err := notes.Save(a.getRequest(), req)
	if err != nil {
		a.report(err)
	}
	return err
}
