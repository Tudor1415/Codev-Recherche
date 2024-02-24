exp_passive = results/exp_passive
exp_active = results/exp_active
exp_passive_ranklib = results/exp_passive_ranklib
exp_active_errors = results/exp_active_errors
exp_eisen = results/exp_eisen
exp_time = results/exp_time
jar_cmd = java -cp target/choquet-rank-1.0.0-jar-with-dependencies.jar

install:
	mvn package
exp_passive:
	rm -rf ${exp_passive}
	mkdir -p ${exp_passive}/learn
	${jar_cmd} io.gitlab.chaver.minimax.experiments.launch.ExperimentPassive
exp_active:
	rm -rf ${exp_active}
	mkdir -p ${exp_active}/learn
	${jar_cmd} io.gitlab.chaver.minimax.experiments.launch.ExperimentActive
exp_passive_ranklib:
	rm -rf ${exp_passive_ranklib}
	mkdir -p ${exp_passive_ranklib}/learn
	${jar_cmd} io.gitlab.chaver.minimax.experiments.launch.ExperimentRankLib
exp_active_errors:
	rm -rf ${exp_active_errors}
	mkdir -p ${exp_active_errors}/learn
	${jar_cmd} io.gitlab.chaver.minimax.experiments.launch.ExperimentErrorsOracles
exp_eisen:
	rm -rf ${exp_eisen}
	mkdir -p ${exp_eisen}/learn
	${jar_cmd} io.gitlab.chaver.minimax.experiments.launch.ExperimentEisen
exp_time:
	rm -rf ${exp_time}
	mkdir -p ${exp_time}/learn
	${jar_cmd} io.gitlab.chaver.minimax.experiments.launch.ExperimentTime
clean:
	mvn clean