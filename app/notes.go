package app

import "hrvanovicm/magacin/internal/notes"

type GetNoteRequest = notes.GetQuery
type SaveNoteRequest = notes.SaveCommand

func (a *WailsApp) GetNote(req GetNoteRequest) (*notes.Note, error) {
	note, err := notes.Get(a.getRequest(), req)
	if err != nil {
		a.report(err)
		return nil, err
	}
	return note, nil
}

func (a *WailsApp) SaveNote(req SaveNoteRequest) error {
	if err := notes.Save(a.getRequest(), req); err != nil {
		a.report(err)
		return err
	}
	return nil
}
