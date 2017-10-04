ALTER TABLE graphs.graph_import_states DROP CONSTRAINT graph_import_states_subscriptions_fk;

ALTER TABLE graphs.graph_import_states
  ADD CONSTRAINT graph_import_states_subscriptions_fk FOREIGN KEY (servername, viewname)
      REFERENCES graphs.subscriptions (servername, viewname) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE CASCADE;

select graphs.db_schema_changed(7, '07_from_v6_to_v7_correct_subscription_fk_constraints.sql');