package io.github.cdelmas.frp.cyclops;

import com.aol.cyclops2.hkt.Higher;
import cyclops.control.lazy.Either;
import cyclops.control.lazy.Either3;
import cyclops.function.Fn1;
import cyclops.monads.Witness;
import cyclops.typeclasses.free.Coyoneda;
import cyclops.typeclasses.free.Free;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

import static java.util.function.Function.identity;

public class FreeMonadExample {
    // free monad example

/*    interface Service<T> {

        Either3<GetTweets, GetUserName, GetUserPhoto> patternMatch();
    }

    @AllArgsConstructor
    final static class Tweet {
        @Getter
        private Integer userId;
        @Getter
        private String msg;
    }

    @AllArgsConstructor
    final static class User {
        @Getter
        private Integer userId;
        @Getter
        private String name;
        @Getter
        private String photo;
    }

    @AllArgsConstructor
    final static class GetTweets implements Service<List<Tweet>> {
        @Getter
        private Integer userId;

        @Override
        public Either3<GetTweets, GetUserName, GetUserPhoto> patternMatch() {
            return Either3.left1(this);
        }

        public <R> R visit(Fn1<Integer, R> getTweets) {
            return getTweets.apply(userId);
        }
    }

    @AllArgsConstructor
    final static class GetUserName implements Service<String> {
        @Getter
        private Integer userId;

        @Override
        public Either3<GetTweets, GetUserName, GetUserPhoto> patternMatch() {
            return Either3.left2(this);
        }

        public <R> R visit(Fn1<Integer, R> getUserName) {
            return getUserName.apply(userId);
        }
    }

    @AllArgsConstructor
    final static class GetUserPhoto implements Service<String> {
        @Getter
        private Integer userId;

        @Override
        public Either3<GetTweets, GetUserName, GetUserPhoto> patternMatch() {
            return Either3.right(this);
        }

        public <R> R visit(Fn1<Integer, R> getUserPhoto) {
            return getUserPhoto.apply(userId);
        }
    }

    abstract static class Request<T> implements Higher<Request.μ, T> {

        static class μ {
        }

        static <T> Request<T> narrowK(Higher<Request.μ, T> higher) {
            return (Request<T>) higher;
        }

        abstract Either<Pure<T>, Fetch<T>> patternMatch();
    }

    final static class Pure<T> extends Request<T> {
        @Getter
        private T t;

        Pure(T t) {
            this.t = t;
        }

        @Override
        Either<Pure<T>, Fetch<T>> patternMatch() {
            return Either.left(this);
        }

        public <R> R visit(Fn1<T, R> pure) {
            return pure.apply(t);
        }
    }

    final static class Fetch<T> extends Request<T> {
        @Getter
        private Service<T> service;

        Fetch(Service<T> service) {
            this.service = service;
        }

        @Override
        Either<Pure<T>, Fetch<T>> patternMatch() {
            return Either.right(this);
        }

        public <R> R visit(Fn1<Service<T>, R> fetch) {
            return fetch.apply(service);
        }
    }

    static class RequestModule {
        static <T, R> Free<Higher<Higher<Witness.coyoneda, Request.μ>, T>, R> pure(T t) {
            return Free.liftF(Coyoneda.of(identity(), new Pure(t)), new Coyoneda.Instances().functor());
        }

        static <T, R> Free<Higher<Higher<Witness.coyoneda, Request.μ>, T>, R> fetch(Service<T> service, Fn1<T, R> transformation) {
            return Free.liftF(Coyoneda.of(transformation, new Fetch<>(service)), new Coyoneda.Instances().functor());
        }
    }

    static class ToyInterpreter {

        public static <R> void interpret(Free<Higher<Higher<Witness.coyoneda, Request.μ>, String>, Void> program){
            //walk the Free data structure and handle each command,
            //by delegating to the appropriate method
            program.resume(new Coyoneda.Instances().functor(), Request::narrowK)
                    .visit(
                            r ->   { matchRequest(r); return null;},
                            __->"\n"
                    );
        }
        private static <R> String matchRequest(Request<Free<Higher<Higher<Witness.coyoneda, Request.μ>, String>, Void>> request) {
            request.patternMatch().visit(
                    ToyInterpreter::handlePure,
                    ToyInterpreter::handleFetch);
            return null;
        }
        static <R> void handlePure(Pure<Free<Higher<Higher<Witness.coyoneda, Request.μ>, String>, Void>> pure){
            pure.visit((a) -> {
                System.out.println("emitted " + a);
                return null;
            });
        }

        static <R> void handleFetch(Fetch<Free<Higher<Higher<Witness.coyoneda, Request.μ>, String>, Void>> fetch){
            fetch.visit(service -> {
                service.patternMatch().visit(
                        getTweets -> getTweets.visit(userId -> tweetsFor(userId)),
                        getUserName -> getUserName.visit(userId -> nameOf(userId)),
                        getUserPhoto -> getUserPhoto.visit(userId -> photoOf(userId))
                );
                return null;
            });
        }

        private static String photoOf(Integer userId) {
            return ":-)";
        }

    }*/
}
