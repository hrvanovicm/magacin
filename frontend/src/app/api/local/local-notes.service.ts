import * as wailsApp from '../../../../wailsjs/go/app/WailsApp';
import {Note, NoteGetRequest, NoteSaveRequest, NotesService} from '../index';

export class LocalNotesService implements NotesService {
  async get(req: NoteGetRequest): Promise<Note> {
    return await wailsApp.GetNote(req as any) as Note;
  }

  async save(req: NoteSaveRequest): Promise<void> {
    return await wailsApp.SaveNote(req as any);
  }
}
