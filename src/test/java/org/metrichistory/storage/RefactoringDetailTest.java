package org.metrichistory.storage;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RefactoringDetailTest {

    @Test
    public void noClassByDefault() {
        final RefactoringDetail detail = new RefactoringDetail();

        assertTrue(detail.getClasses().isEmpty());
    }

    @Test
    public void parseRenameMethod() {
        final RefactoringDetail detail = new RefactoringDetail();

        detail.addRefactoring("Rename method", "\" Rename Method	public issuedBy(issuedBy HeldCertificate) : Builder renamed to public signedBy(signedBy HeldCertificate) : Builder in class okhttp3.tls.HeldCertificate.Builder\"");

        assertTrue(detail.getClasses().contains("okhttp3.tls.HeldCertificate.Builder"));
        assertEquals(1, detail.getClasses().size());
    }

    @Test
    public void parseRenameClass() {
        final RefactoringDetail detail = new RefactoringDetail();

        detail.addRefactoring("Rename class", "\"Rename Class	okhttp3.tls.TlsNode renamed to okhttp3.tls.HandshakeCertificates\"");

        assertTrue(detail.getClasses().contains("okhttp3.tls.TlsNode"));
        assertTrue(detail.getClasses().contains("okhttp3.tls.HandshakeCertificates"));
        assertEquals(2, detail.getClasses().size());
    }

    @Test
    public void parseMoveClass() {
        final RefactoringDetail detail = new RefactoringDetail();

        detail.addRefactoring("Move class", "\"Move Class okhttp3.mockwebserver.HeldCertificate moved to okhttp3.tls.HeldCertificate\"");

        assertTrue(detail.getClasses().contains("okhttp3.mockwebserver.HeldCertificate"));
        assertTrue(detail.getClasses().contains("okhttp3.tls.HeldCertificate"));
        assertEquals(2, detail.getClasses().size());
    }

    @Test
    public void parseExtractMethod() {
        final RefactoringDetail detail = new RefactoringDetail();

        detail.addRefactoring("Extract method", "\"Extract Method public get(string String) : MediaType extracted from public parse(string String) : MediaType in class okhttp3.MediaType\"");

        assertTrue(detail.getClasses().contains("okhttp3.MediaType"));
        assertEquals(1, detail.getClasses().size());
    }

    @Test
    public void parseMoveAttribute() {
        final RefactoringDetail detail = new RefactoringDetail();

        detail.addRefactoring("Move attribute", "\"Move Attribute private addSuppressedExceptionMethod : Method from class okhttp3.internal.connection.RouteException to class okhttp3.internal.Util\"");

        assertTrue(detail.getClasses().contains("okhttp3.internal.connection.RouteException"));
        assertTrue(detail.getClasses().contains("okhttp3.internal.Util"));
        assertEquals(2, detail.getClasses().size());
    }

    @Test
    public void parseInlineMethod() {
        final RefactoringDetail detail = new RefactoringDetail();

        detail.addRefactoring("Inline method", "\"Inline Method private checkNotClosed() : void inlined to public read(sink Buffer, byteCount long) : long in class okhttp3.internal.http2.Http2Stream.FramingSource\"");

        assertTrue(detail.getClasses().contains("okhttp3.internal.http2.Http2Stream.FramingSource"));
        assertEquals(1, detail.getClasses().size());
    }

    @Test
    public void parseExtractAndMoveMethod() {
        final RefactoringDetail detail = new RefactoringDetail();

        detail.addRefactoring("Extract And Move Method", "\"Extract And Move Method public withCharset(charset Charset) : Challenge extracted from public parseChallenges(responseHeaders Headers, challengeHeader String) : List<Challenge> in class okhttp3.internal.http.HttpHeaders & moved to class okhttp3.Challenge\"");

        assertTrue(detail.getClasses().contains("okhttp3.internal.http.HttpHeaders"));
        assertTrue(detail.getClasses().contains("okhttp3.Challenge"));
        assertEquals(2, detail.getClasses().size());
    }

    @Test
    public void parsePullUpAttribute() {
        final RefactoringDetail detail = new RefactoringDetail();

        detail.addRefactoring("Pull Up Attribute", "\"Pull Up Attribute private source : BufferedSource from class okhttp3.internal.framed.Http2.Reader to class okhttp3.internal.framed.FrameReader\"");

        assertTrue(detail.getClasses().contains("okhttp3.internal.framed.Http2.Reader"));
        assertTrue(detail.getClasses().contains("okhttp3.internal.framed.FrameReader"));
        assertEquals(2, detail.getClasses().size());
    }

    @Test
    public void parsePushDownAttribute() {
        final RefactoringDetail detail = new RefactoringDetail();

        detail.addRefactoring("Push Down Attribute", "\"Push Down Attribute\tprivate MAX_SIGNERS : int from class okhttp3.internal.tls.CertificateChainCleaner to class okhttp3.internal.tls.CertificateChainCleaner.BasicCertificateChainCleaner\"");

        assertTrue(detail.getClasses().contains("okhttp3.internal.tls.CertificateChainCleaner"));
        assertTrue(detail.getClasses().contains("okhttp3.internal.tls.CertificateChainCleaner.BasicCertificateChainCleaner"));
        assertEquals(2, detail.getClasses().size());
    }

    @Test
    public void parsePullUpMethod() {
        final RefactoringDetail detail = new RefactoringDetail();

        detail.addRefactoring("Pull Up Method", "\"Pull Up Method\tpublic readConnectionPreface() : void from class okhttp3.internal.framed.Http2.Reader to public readConnectionPreface() : void from class okhttp3.internal.framed.FrameReader\"");

        assertTrue(detail.getClasses().contains("okhttp3.internal.framed.Http2.Reader"));
        assertTrue(detail.getClasses().contains("okhttp3.internal.framed.FrameReader"));
        assertEquals(2, detail.getClasses().size());
    }

    @Test
    public void parsePushDownMethod() {
        final RefactoringDetail detail = new RefactoringDetail();

        detail.addRefactoring("Push Down Method", "\"Push Down Method\tpublic CertificateChainCleaner(trustRootIndex TrustRootIndex) from class okhttp3.internal.tls.CertificateChainCleaner to public BasicCertificateChainCleaner(trustRootIndex TrustRootIndex) from class okhttp3.internal.tls.CertificateChainCleaner.BasicCertificateChainCleaner\"");

        assertTrue(detail.getClasses().contains("okhttp3.internal.tls.CertificateChainCleaner"));
        assertTrue(detail.getClasses().contains("okhttp3.internal.tls.CertificateChainCleaner.BasicCertificateChainCleaner"));
        assertEquals(2, detail.getClasses().size());
    }

    @Test
    public void parseExtractSuperclass() {
        final RefactoringDetail detail = new RefactoringDetail();

        detail.addRefactoring("Extract Superclass", "Extract Superclass retrofit2.HttpException from classes [retrofit2.adapter.guava.HttpException, retrofit2.adapter.java8.HttpException, retrofit2.adapter.rxjava.HttpException, retrofit2.adapter.rxjava2.HttpException]");

        assertTrue(detail.getClasses().contains("retrofit2.HttpException"));
        assertTrue(detail.getClasses().contains("retrofit2.adapter.guava.HttpException"));
        assertTrue(detail.getClasses().contains("retrofit2.adapter.java8.HttpException"));
        assertTrue(detail.getClasses().contains("retrofit2.adapter.rxjava.HttpException"));
        assertTrue(detail.getClasses().contains("retrofit2.adapter.rxjava2.HttpException"));
        assertEquals(5, detail.getClasses().size());
    }

    @Test
    public void parsePullUpMethodNoSpace() {
        final RefactoringDetail detail = new RefactoringDetail();

        detail.addRefactoring("Pull Up Method", "Pull Up Methodpublic code() : int from class retrofit2.adapter.java8.HttpException to public code() : int from class retrofit2.HttpException");

        assertTrue(detail.getClasses().contains("retrofit2.HttpException"));
        assertTrue(detail.getClasses().contains("retrofit2.adapter.java8.HttpException"));
        assertEquals(2, detail.getClasses().size());
    }
}
