export interface ZendeskPlugin {
  echo(options: { value: string }): Promise<{ value: string }>;
}
