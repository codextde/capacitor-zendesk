import { WebPlugin } from '@capacitor/core';

import type { ZendeskPlugin } from './definitions';

export class ZendeskWeb extends WebPlugin implements ZendeskPlugin {
  async echo(options: { value: string }): Promise<{ value: string }> {
    console.log('ECHO', options);
    return options;
  }
}
