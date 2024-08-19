.PHONY: nothing
nothing:
	@echo What are you doing?

.PHONY: end-to-end
end-to-end: cleanup-e2e
	@docker compose -f test/docker-compose.yml build --build-arg=GITHUB_USERNAME=$(GITHUB_USERNAME) --build-arg=GITHUB_TOKEN=$(GITHUB_TOKEN)
	@docker compose -f test/docker-compose.yml run test || true
	@docker compose -f test/docker-compose.yml down

.PHONY: cleanup-e2e
cleanup-e2e:
	@docker compose -f test/docker-compose.yml down
